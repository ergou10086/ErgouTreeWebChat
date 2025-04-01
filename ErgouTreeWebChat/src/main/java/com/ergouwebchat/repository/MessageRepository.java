package com.ergouwebchat.repository;

import com.ergouwebchat.model.entity.Conversation;
import com.ergouwebchat.model.entity.Message;
import com.ergouwebchat.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息数据访问层
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    
    /**
     * 查找会话中的消息，按时间排序
     * @param conversation 会话
     * @param pageable 分页参数
     * @return 消息分页结果
     */
    Page<Message> findByConversationOrderByCreatedAtDesc(Conversation conversation, Pageable pageable);
    
    /**
     * 查找会话中的最新消息
     * @param conversation 会话
     * @param limit 消息数量限制
     * @return 消息列表
     */
    @Query("SELECT m FROM Message m WHERE m.conversation = ?1 ORDER BY m.createdAt DESC")
    List<Message> findLatestMessages(Conversation conversation, Pageable pageable);
    
    /**
     * 查找用户发送的消息
     * @param sender 发送者
     * @return 消息列表
     */
    List<Message> findBySender(User sender);
    
    /**
     * 查找特定时间段内的消息
     * @param conversation 会话
     * @param start 开始时间
     * @param end 结束时间
     * @return 消息列表
     */
    List<Message> findByConversationAndCreatedAtBetweenOrderByCreatedAtAsc(
            Conversation conversation, LocalDateTime start, LocalDateTime end);
    
    /**
     * 统计会话中的消息数量
     * @param conversation 会话
     * @return 消息数量
     */
    long countByConversation(Conversation conversation);
    
    /**
     * 删除会话中的所有消息
     * @param conversation 会话
     */
    void deleteByConversation(Conversation conversation);
}
