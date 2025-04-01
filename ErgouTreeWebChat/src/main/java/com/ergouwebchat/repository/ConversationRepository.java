package com.ergouwebchat.repository;

import com.ergouwebchat.model.entity.Conversation;
import com.ergouwebchat.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 会话数据访问层
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    /**
     * 查找用户参与的所有会话
     * @param user 用户
     * @return 会话列表
     */
    List<Conversation> findByParticipantsContaining(User user);
    
    /**
     * 查找两个用户之间的私聊会话
     * @param type 会话类型
     * @param user1 用户1
     * @param user2 用户2
     * @return 私聊会话
     */
    @Query("SELECT c FROM Conversation c WHERE c.type = :type AND SIZE(c.participants) = 2 AND :user1 MEMBER OF c.participants AND :user2 MEMBER OF c.participants")
    Optional<Conversation> findPrivateConversation(Conversation.ConversationType type, User user1, User user2);
    
    /**
     * 查找群聊会话
     * @param type 会话类型
     * @param name 群聊名称
     * @return 群聊会话
     */
    Optional<Conversation> findByTypeAndName(Conversation.ConversationType type, String name);
}
