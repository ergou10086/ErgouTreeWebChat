package com.ergouwebchat.util;

import com.ergouwebchat.model.Message;
import com.ergouwebchat.model.enums.MessageType;

import java.util.regex.Pattern;

/**
 * 消息验证工具类
 * <p>提供消息内容验证和安全过滤功能</p>
 */
public class MessageValidator {
    // 最大消息长度限制（字符数）
    private static final int MAX_MESSAGE_LENGTH = 1000;
    
    // 用户名格式正则表达式（字母、数字、下划线，长度3-20）
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^\\w{3,20}$");
    
    // XSS攻击常见特征正则表达式
    private static final Pattern XSS_PATTERN = Pattern.compile("<script.*?>|<.*?javascript:.*?>|<.*?\\s+on\\w+\\s*=.*?>", 
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    
    /**
     * 私有构造函数，防止实例化
     */
    private MessageValidator() {
        throw new AssertionError("工具类不应被实例化");
    }
    
    /**
     * 验证消息是否有效
     * @param message 要验证的消息
     * @return 如果消息有效则返回true
     */
    public static boolean isValid(Message message) {
        if (message == null) {
            return false;
        }
        
        // 验证必要字段
        if (message.getType() == null || 
            message.getSender() == null || 
            message.getContent() == null || 
            message.getTimestamp() == null) {
            return false;
        }
        
        // 验证内容长度
        if (message.getContent().length() > MAX_MESSAGE_LENGTH) {
            return false;
        }
        
        // 验证用户名格式（系统消息除外）
        if (!message.isSystemMessage() && !isValidUsername(message.getSender())) {
            return false;
        }
        
        // 检查XSS攻击
        if (containsXss(message.getContent())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证用户名格式
     * @param username 用户名
     * @return 如果用户名格式有效则返回true
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 检测内容是否包含XSS攻击特征
     * @param content 要检查的内容
     * @return 如果包含XSS攻击特征则返回true
     */
    public static boolean containsXss(String content) {
        return content != null && XSS_PATTERN.matcher(content).find();
    }
    
    /**
     * 过滤消息内容中的XSS特征
     * @param content 原始内容
     * @return 过滤后的安全内容
     */
    public static String sanitizeContent(String content) {
        if (content == null) {
            return "";
        }
        
        // 替换HTML特殊字符
        String sanitized = content
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
        
        return sanitized;
    }
    
    /**
     * 验证并清理消息
     * @param message 原始消息
     * @return 清理后的消息，如果消息无效则返回null
     */
    public static Message validateAndSanitize(Message message) {
        if (!isValid(message)) {
            return null;
        }
        
        // 清理消息内容
        message.setContent(sanitizeContent(message.getContent()));
        
        return message;
    }
}
