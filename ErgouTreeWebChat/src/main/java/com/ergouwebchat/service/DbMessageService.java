package com.ergouwebchat.service;

import com.ergouwebchat.model.entity.Conversation;
import com.ergouwebchat.model.entity.Message;
import com.ergouwebchat.model.entity.User;
import com.ergouwebchat.model.enums.MessageType;
import com.ergouwebchat.repository.ConversationRepository;
import com.ergouwebchat.repository.MessageRepository;
import com.ergouwebchat.repository.UserRepository;
import com.ergouwebchat.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * 消息数据库服务类
 */
@Service
public class DbMessageService {
    
    private static final Logger LOGGER = Logger.getLogger(DbMessageService.class.getName());
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    
    @Autowired
    public DbMessageService(MessageRepository messageRepository, 
                          UserRepository userRepository,
                          ConversationRepository conversationRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }
    
    /**
     * 保存消息到数据库
     * @param messageId 消息ID
     * @param senderUsername 发送者用户名
     * @param recipientUsername 接收者用户名（私聊时）
     * @param content 消息内容
     * @param type 消息类型
     * @param metadata 消息元数据
     * @return 保存的消息
     */
    @Transactional
    public Message saveMessage(String messageId, String senderUsername, String recipientUsername, 
                              String content, MessageType type, Map<String, Object> metadata) {
        // 查找发送者
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("发送者不存在"));
        
        // 获取或创建会话
        Conversation conversation;
        
        if (recipientUsername != null && !recipientUsername.equals("GROUP")) {
            // 私聊
            User recipient = userRepository.findByUsername(recipientUsername)
                    .orElseThrow(() -> new RuntimeException("接收者不存在"));
            
            // 查找或创建私聊会话
            conversation = conversationRepository
                    .findPrivateConversation(Conversation.ConversationType.PRIVATE, sender, recipient)
                    .orElseGet(() -> {
                        Conversation newConversation = new Conversation();
                        newConversation.setType(Conversation.ConversationType.PRIVATE);
                        newConversation.getParticipants().add(sender);
                        newConversation.getParticipants().add(recipient);
                        return conversationRepository.save(newConversation);
                    });
        } else {
            // 群聊
            // 查找或创建群聊会话
            conversation = conversationRepository
                    .findByTypeAndName(Conversation.ConversationType.GROUP, "群聊")
                    .orElseGet(() -> {
                        Conversation newConversation = new Conversation();
                        newConversation.setType(Conversation.ConversationType.GROUP);
                        newConversation.setName("群聊");
                        // 添加所有在线用户
                        List<User> onlineUsers = userRepository.findByStatus(User.UserStatus.ONLINE);
                        newConversation.getParticipants().addAll(onlineUsers);
                        return conversationRepository.save(newConversation);
                    });
        }
        
        // 创建消息
        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);
        message.setConversation(conversation);
        message.setType(type);
        message.setContent(content);
        
        // 设置元数据
        if (metadata != null) {
            message.setMetadata(JsonUtils.toJson(metadata));
        }
        
        message.setCreatedAt(LocalDateTime.now());
        
        // 更新会话的最后更新时间
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        // 保存消息
        return messageRepository.save(message);
    }
    
    /**
     * 获取会话的最近消息
     * @param conversationId 会话ID
     * @param limit 消息数量限制
     * @return 消息列表
     */
    public List<Message> getRecentMessages(Long conversationId, int limit) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        return messageRepository.findLatestMessages(
                conversation, 
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }
    
    /**
     * 获取用户的所有会话
     * @param username 用户名
     * @return 会话列表
     */
    public List<Conversation> getUserConversations(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return conversationRepository.findByParticipantsContaining(user);
    }
    
    /**
     * 将消息标记为已读
     * @param messageId 消息ID
     * @param username 用户名
     */
    @Transactional
    public void markMessageAsRead(String messageId, String username) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 将消息添加到用户的已读消息列表中
        message.getReadBy().add(user);
        messageRepository.save(message);
    }
    
    /**
     * 获取消息的已读用户列表
     * @param messageId 消息ID
     * @return 已读用户列表
     */
    public Set<User> getMessageReadBy(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        
        return message.getReadBy();
    }
    
    /**
     * 删除会话的所有消息
     * @param conversationId 会话ID
     */
    @Transactional
    public void deleteConversationMessages(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));
        
        messageRepository.deleteByConversation(conversation);
    }
}
