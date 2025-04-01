package com.ergouwebchat.service;

import com.ergouwebchat.model.Message;
import com.ergouwebchat.model.enums.MessageType;
import com.ergouwebchat.service.factory.MessageFactory;
import com.ergouwebchat.util.JsonUtils;
import com.ergouwebchat.util.MessageValidator;

import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 消息处理中枢类
 * <p>负责消息的验证、处理和分发</p>
 */
@Service
public class MessageService {
    private static final Logger LOGGER = Logger.getLogger(MessageService.class.getName());
    
    private final MessageBroadcaster messageBroadcaster;
    private final UserSessionManager sessionManager;
    private final List<Message> messageHistory;
    private static final int MAX_HISTORY_SIZE = 100;
    
    @Autowired
    private DbMessageService dbMessageService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 构造函数
     */
    public MessageService() {
        this.messageBroadcaster = new MessageBroadcaster();
        this.sessionManager = UserSessionManager.getInstance();
        this.messageHistory = new ArrayList<>();
    }
    
    /**
     * 处理接收到的消息
     * @param jsonMessage JSON格式的消息字符串
     * @param session 发送消息的WebSocket会话
     * @return 处理是否成功
     */
    public boolean processIncomingMessage(String jsonMessage, Session session) {
        // 解析JSON消息
        Message message = JsonUtils.fromJson(jsonMessage, Message.class);
        if (message == null) {
            LOGGER.warning("无法解析消息: " + jsonMessage);
            return false;
        }
        
        // 验证消息内容
        Message validatedMessage = MessageValidator.validateAndSanitize(message);
        if (validatedMessage == null) {
            LOGGER.warning("消息验证失败: " + jsonMessage);
            return false;
        }
        
        // 处理消息
        return process(validatedMessage);
    }
    
    /**
     * 处理消息对象
     * @param message 消息对象
     * @return 处理是否成功
     */
    public boolean process(Message message) {
        try {
            // 根据消息类型进行处理
            switch (message.getType()) {
                case TEXT:
                    return processTextMessage(message);
                    
                case IMAGE:
                    return processImageMessage(message);
                    
                case FILE:
                    return processFileMessage(message);
                    
                case SYSTEM_NOTICE:
                    return processSystemMessage(message);
                    
                case USER_JOIN:
                    return processUserJoinMessage(message);
                    
                case USER_LEAVE:
                    return processUserLeaveMessage(message);
                    
                case TYPING:
                    return processTypingMessage(message);
                    
                case READ_RECEIPT:
                    return processReadReceiptMessage(message);
                    
                default:
                    LOGGER.warning("不支持的消息类型: " + message.getType());
                    return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "处理消息时发生错误: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 处理文本消息
     * @param message 文本消息
     * @return 处理是否成功
     */
    private boolean processTextMessage(Message message) {
        // 检查是否为私聊消息
        if (message.getRecipient() != null && !message.getRecipient().isEmpty()) {
            // 私聊消息
            String recipient = message.getRecipient();
            
            // 检查接收者是否在线
            if (!sessionManager.isUserOnline(recipient)) {
                // 接收者不在线，发送错误消息给发送者
                Message errorMessage = MessageFactory.createSystemMessage("用户 " + recipient + " 不在线，无法发送私信");
                return messageBroadcaster.sendToUser(message.getSender(), errorMessage);
            }
            
            // 发送给接收者
            boolean sentToRecipient = messageBroadcaster.sendToUser(recipient, message);
            
            // 同时发送给发送者（确认消息已发送）
            boolean sentToSender = messageBroadcaster.sendToUser(message.getSender(), message);
            
            // 添加到历史记录
            addToHistory(message);
            
            // 保存到数据库
            saveMessageToDatabase(message);
            
            return sentToRecipient && sentToSender;
        } else {
            // 群聊消息，广播给所有用户
            messageBroadcaster.broadcastToAll(message);
            
            // 添加到历史记录
            addToHistory(message);
            
            // 保存到数据库
            saveMessageToDatabase(message);
            
            return true;
        }
    }
    
    /**
     * 处理图片消息
     * @param message 图片消息
     * @return 处理是否成功
     */
    private boolean processImageMessage(Message message) {
        // 图片消息处理逻辑与文本消息类似
        return processTextMessage(message);
    }
    
    /**
     * 处理文件消息
     * @param message 文件消息
     * @return 处理是否成功
     */
    private boolean processFileMessage(Message message) {
        // 文件消息处理逻辑与文本消息类似
        return processTextMessage(message);
    }
    
    /**
     * 处理系统消息
     * @param message 系统消息
     * @return 处理是否成功
     */
    private boolean processSystemMessage(Message message) {
        // 广播系统消息给所有用户
        messageBroadcaster.broadcastToAll(message);
        
        // 添加到历史记录
        addToHistory(message);
        
        // 保存到数据库
        saveMessageToDatabase(message);
        
        return true;
    }
    
    /**
     * 处理用户加入消息
     * @param message 用户加入消息
     * @return 处理是否成功
     */
    private boolean processUserJoinMessage(Message message) {
        // 广播用户加入消息给所有用户
        messageBroadcaster.broadcastToAll(message);
        
        // 添加到历史记录
        addToHistory(message);
        
        // 向新用户发送当前在线用户列表
        String joinedUser = (String) message.getMetadata().get("joinedUser");
        if (joinedUser != null) {
            Message userListMessage = createUserListMessage();
            messageBroadcaster.sendToUser(joinedUser, userListMessage);
            
            // 发送最近的消息历史
            sendMessageHistory(joinedUser);
        }
        
        return true;
    }
    
    /**
     * 处理用户离开消息
     * @param message 用户离开消息
     * @return 处理是否成功
     */
    private boolean processUserLeaveMessage(Message message) {
        // 广播用户离开消息给所有用户
        messageBroadcaster.broadcastToAll(message);
        
        // 添加到历史记录
        addToHistory(message);
        
        return true;
    }
    
    /**
     * 处理正在输入状态消息
     * @param message 输入状态消息
     * @return 处理是否成功
     */
    private boolean processTypingMessage(Message message) {
        // 检查是否为私聊消息
        if (message.getRecipient() != null && !message.getRecipient().isEmpty() && !"GROUP".equals(message.getRecipient())) {
            // 私聊输入状态，只发送给特定用户
            String recipient = message.getRecipient();
            
            // 检查接收者是否在线
            if (!sessionManager.isUserOnline(recipient)) {
                return false; // 接收者不在线，忽略消息
            }
            
            // 发送给接收者
            return messageBroadcaster.sendToUser(recipient, message);
        } else {
            // 群聊输入状态，广播给除发送者外的所有用户
            return messageBroadcaster.broadcastToAllExcept(message, message.getSender());
        }
    }
    
    /**
     * 处理已读回执消息
     * @param message 已读回执消息
     * @return 处理是否成功
     */
    private boolean processReadReceiptMessage(Message message) {
        // 已读回执只发送给原消息的发送者
        if (message.getRecipient() != null && !message.getRecipient().isEmpty()) {
            String recipient = message.getRecipient();
            
            // 检查接收者是否在线
            if (!sessionManager.isUserOnline(recipient)) {
                return false; // 接收者不在线，忽略消息
            }
            
            // 发送给接收者
            boolean sent = messageBroadcaster.sendToUser(recipient, message);
            
            // 更新数据库中的消息已读状态
            if (sent && message.getContent() != null && !message.getContent().isEmpty()) {
                // 消息ID存储在content字段中
                String messageId = message.getContent();
                dbMessageService.markMessageAsRead(messageId, message.getSender());
            }
            
            return sent;
        }
        
        return false;
    }
    
    /**
     * 创建用户加入消息
     * @param username 加入的用户名
     * @return 用户加入消息
     */
    public Message createUserJoinMessage(String username) {
        return MessageFactory.createUserJoinMessage(username);
    }
    
    /**
     * 创建用户离开消息
     * @param username 离开的用户名
     * @return 用户离开消息
     */
    public Message createUserLeaveMessage(String username) {
        return MessageFactory.createUserLeaveMessage(username);
    }
    
    /**
     * 创建在线用户列表消息
     * @return 包含在线用户列表的系统消息
     */
    public Message createUserListMessage() {
        StringBuilder content = new StringBuilder("当前在线用户: ");
        Iterable<String> usernames = sessionManager.getUsernames();
        
        boolean first = true;
        for (String username : usernames) {
            if (!first) {
                content.append(", ");
            } else {
                first = false;
            }
            content.append(username);
        }
        
        Message message = MessageFactory.createSystemMessage(content.toString());
        message.addMetadata("userCount", sessionManager.getActiveUserCount());
        message.addMetadata("userList", usernames);
        
        return message;
    }
    
    /**
     * 添加消息到历史记录
     * @param message 要添加的消息
     */
    private synchronized void addToHistory(Message message) {
        messageHistory.add(message);
        
        // 限制历史记录大小
        if (messageHistory.size() > MAX_HISTORY_SIZE) {
            messageHistory.remove(0);
        }
    }
    
    /**
     * 保存消息到数据库
     * @param message 要保存的消息
     */
    private void saveMessageToDatabase(Message message) {
        try {
            // 只保存需要持久化的消息类型
            if (message.getType() == MessageType.TEXT || 
                message.getType() == MessageType.IMAGE || 
                message.getType() == MessageType.FILE || 
                message.getType() == MessageType.SYSTEM_NOTICE) {
                
                // 准备元数据
                Map<String, Object> metadata = message.getMetadata() != null ? 
                    message.getMetadata() : new HashMap<>();
                
                // 保存消息到数据库
                dbMessageService.saveMessage(
                    message.getMessageId(),
                    message.getSender(),
                    message.getRecipient(),
                    message.getContent(),
                    message.getType(),
                    metadata
                );
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "保存消息到数据库失败: " + e.getMessage(), e);
            // 数据库保存失败不影响消息的实时传递
        }
    }
    
    /**
     * 发送消息历史记录给指定用户
     * @param username 目标用户名
     */
    private void sendMessageHistory(String username) {
        // 只发送最近的20条消息
        int historySize = messageHistory.size();
        int startIndex = Math.max(0, historySize - 20);
        
        // 创建历史消息列表
        List<Message> recentMessages = messageHistory.subList(startIndex, historySize);
        
        // 发送历史消息
        for (Message historyMessage : recentMessages) {
            messageBroadcaster.sendToUser(username, historyMessage);
        }
    }
    
    /**
     * 广播系统通知
     * @param content 通知内容
     */
    public void broadcastSystemNotice(String content) {
        Message systemMessage = MessageFactory.createSystemMessage(content);
        messageBroadcaster.broadcastToAll(systemMessage);
        addToHistory(systemMessage);
    }
}
