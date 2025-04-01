package com.ergouwebchat.model;

import com.ergouwebchat.model.enums.MessageType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 聊天消息实体类
 * <p>包含消息类型、发送者、内容、时间戳和元数据等核心属性</p>
 */
@Setter
@Getter
public class Message {
    // getters and setters
    /**
     * 消息唯一标识
     */
    private String messageId;

    /**
     * 消息类型
     * @see MessageType
     * @required 必须指定消息类型
     */
    @NotNull
    private MessageType type;

    /**
     * 发送者标识
     * @required 不可为空字符串
     */
    @NotBlank
    private String sender;

    /**
     * 接收者标识（私聊时使用，群聊为null）
     */
    private String recipient;

    /**
     * 消息内容主体
     * @required 不可为空字符串
     */
    @NotBlank
    private String content;

    /**
     * 消息创建时间戳
     * @required 必须包含有效时间信息
     */
    @NotNull
    private LocalDateTime timestamp;

    /**
     * 扩展元数据
     * <p>可包含附件信息、消息状态等额外属性</p>
     */
    private Map<String, Object> metadata;

    /**
     * 消息是否已读
     */
    private boolean read;

    /**
     * 消息是否已送达
     */
    private boolean delivered;

    /**
     * 无参构造方法
     */
    public Message() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
        this.read = false;
        this.delivered = false;
    }

    /**
     * 创建文本消息的工厂方法
     * @param sender 发送者标识
     * @param content 消息内容
     * @return 新的文本消息实例
     */
    public static Message createTextMessage(String sender, String content) {
        Message message = new Message();
        message.setType(MessageType.TEXT);
        message.setSender(sender);
        message.setContent(content);
        return message;
    }

    /**
     * 创建系统通知消息的工厂方法
     * @param content 通知内容
     * @return 新的系统通知消息实例
     */
    public static Message createSystemMessage(String content) {
        Message message = new Message();
        message.setType(MessageType.SYSTEM_NOTICE);
        message.setSender("SYSTEM");
        message.setContent(content);
        return message;
    }

    /**
     * 全参构造方法
     * @param type 消息类型枚举
     * @param sender 发送者标识（非空）
     * @param content 消息内容（非空）
     * @param timestamp 消息时间（非空）
     * @throws IllegalArgumentException 当参数不符合校验规则时抛出
     */
    public Message(MessageType type, String sender, String content, LocalDateTime timestamp) {
        this.messageId = UUID.randomUUID().toString();
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.metadata = new HashMap<>();
        this.read = false;
        this.delivered = false;
    }

    /**
     * 添加元数据项
     * @param key 元数据键
     * @param value 元数据值
     */
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }

    /**
     * 判断消息是否为系统消息
     * @return 如果是系统消息则返回true
     */
    public boolean isSystemMessage() {
        return MessageType.SYSTEM_NOTICE.equals(this.type) ||
                MessageType.USER_JOIN.equals(this.type) ||
                MessageType.USER_LEAVE.equals(this.type);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", type=" + type +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", metadata=" + metadata +
                ", read=" + read +
                ", delivered=" + delivered +
                '}';
    }

    /**
     * 获取格式化时间字符串
     * @return 按yyyy-MM-dd HH:mm格式返回时间
     */
    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return timestamp.format(formatter);
    }


}
