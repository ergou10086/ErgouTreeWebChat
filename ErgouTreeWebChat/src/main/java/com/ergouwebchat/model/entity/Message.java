package com.ergouwebchat.model.entity;

import com.ergouwebchat.model.enums.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Entity
@Table(name = "messages")
@Data
public class Message {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "JSON")
    private String metadata;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToMany
    @JoinTable(
        name = "message_read_status",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private java.util.Set<User> readBy = new java.util.HashSet<>();
}
