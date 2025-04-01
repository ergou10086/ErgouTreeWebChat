package com.ergouwebchat.service.factory;

import com.ergouwebchat.model.Message;
import com.ergouwebchat.model.enums.MessageType;

import java.time.LocalDateTime;

/**
 * 消息工厂类
 * <p>负责创建不同类型的消息对象，实现工厂模式</p>
 */
public class MessageFactory {
    
    /**
     * 私有构造函数，防止实例化
     */
    private MessageFactory() {
        throw new AssertionError("工厂类不应被实例化");
    }
    
    /**
     * 创建文本消息
     * @param sender 发送者用户名
     * @param content 消息内容
     * @return 文本消息对象
     */
    public static Message createTextMessage(String sender, String content) {
        Message message = new Message();
        message.setType(MessageType.TEXT);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    /**
     * 创建私聊文本消息
     * @param sender 发送者用户名
     * @param recipient 接收者用户名
     * @param content 消息内容
     * @return 私聊文本消息对象
     */
    public static Message createPrivateTextMessage(String sender, String recipient, String content) {
        Message message = createTextMessage(sender, content);
        message.setRecipient(recipient);
        return message;
    }
    
    /**
     * 创建系统通知消息
     * @param content 通知内容
     * @return 系统通知消息对象
     */
    public static Message createSystemMessage(String content) {
        Message message = new Message();
        message.setType(MessageType.SYSTEM_NOTICE);
        message.setSender("SYSTEM");
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return message;
    }
    
    /**
     * 创建用户加入消息
     * @param username 加入的用户名
     * @return 用户加入消息对象
     */
    public static Message createUserJoinMessage(String username) {
        Message message = new Message();
        message.setType(MessageType.USER_JOIN);
        message.setSender("SYSTEM");
        message.setContent(username + " 加入了聊天室");
        message.setTimestamp(LocalDateTime.now());
        message.addMetadata("joinedUser", username);
        return message;
    }
    
    /**
     * 创建用户离开消息
     * @param username 离开的用户名
     * @return 用户离开消息对象
     */
    public static Message createUserLeaveMessage(String username) {
        Message message = new Message();
        message.setType(MessageType.USER_LEAVE);
        message.setSender("SYSTEM");
        message.setContent(username + " 离开了聊天室");
        message.setTimestamp(LocalDateTime.now());
        message.addMetadata("leftUser", username);
        return message;
    }
    
    /**
     * 创建图片消息
     * @param sender 发送者用户名
     * @param imageUrl 图片URL
     * @param caption 图片说明（可选）
     * @return 图片消息对象
     */
    public static Message createImageMessage(String sender, String imageUrl, String caption) {
        Message message = new Message();
        message.setType(MessageType.IMAGE);
        message.setSender(sender);
        message.setContent(caption != null ? caption : "");
        message.setTimestamp(LocalDateTime.now());
        message.addMetadata("imageUrl", imageUrl);
        return message;
    }
    
    /**
     * 创建文件消息
     * @param sender 发送者用户名
     * @param fileUrl 文件URL
     * @param fileName 文件名
     * @param fileSize 文件大小（字节）
     * @return 文件消息对象
     */
    public static Message createFileMessage(String sender, String fileUrl, String fileName, long fileSize) {
        Message message = new Message();
        message.setType(MessageType.FILE);
        message.setSender(sender);
        message.setContent("分享了文件: " + fileName);
        message.setTimestamp(LocalDateTime.now());
        message.addMetadata("fileUrl", fileUrl);
        message.addMetadata("fileName", fileName);
        message.addMetadata("fileSize", fileSize);
        return message;
    }
}
