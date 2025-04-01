package com.ergouwebchat.repository;

import com.ergouwebchat.model.entity.FileEntity;
import com.ergouwebchat.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文件数据访问层
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    
    /**
     * 根据消息查找文件
     * @param message 消息
     * @return 文件列表
     */
    List<FileEntity> findByMessage(Message message);
    
    /**
     * 根据文件类型查找文件
     * @param fileType 文件类型
     * @return 文件列表
     */
    List<FileEntity> findByFileType(String fileType);
    
    /**
     * 删除消息相关的所有文件
     * @param message 消息
     */
    void deleteByMessage(Message message);
}
