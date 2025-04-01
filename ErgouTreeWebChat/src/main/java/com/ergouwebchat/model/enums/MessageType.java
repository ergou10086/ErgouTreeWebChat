package com.ergouwebchat.model.enums;

/**
 * 消息类型枚举
 * <p>定义系统支持的各种消息类型</p>
 */
public enum MessageType {
    /**
     * 文本消息
     */
    TEXT,       // 文本消息

    /**
     * 图片消息
     */
    IMAGE,      // 图片消息

    /**
     * 文件消息
     */
    FILE,       // 文件消息

    /**
     * 系统通知消息
     */
    SYSTEM_NOTICE,  // 系统通知

    /**
     * 用户加入消息
     */
    USER_JOIN,  // 用户加入

    /**
     * 用户离开消息
     */
    USER_LEAVE, // 用户离开

    /**
     * 正在输入状态
     */
    TYPING,     // 正在输入状态

    /**
     * 已读回执
     */
    READ_RECEIPT // 已读回执
}
