package com.ergouwebchat.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件实体类
 */
@Entity
@Table(name = "files")
@Data
public class FileEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "file_type", nullable = false)
    private String fileType;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
