package com.ergouwebchat.service;

import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户会话管理器（单例模式）
 * <p>负责管理所有WebSocket连接的用户会话</p>
 */
public class UserSessionManager {
    // 单例实例
    private static UserSessionManager instance;
    
    // 存储活跃用户会话的线程安全Map
    private final Map<String, Session> activeSessions;
    
    // 存储用户名和用户ID的映射关系
    private final Map<String, String> usernameToSessionId;
    
    /**
     * 私有构造函数，防止外部实例化
     */
    private UserSessionManager() {
        this.activeSessions = new ConcurrentHashMap<>();
        this.usernameToSessionId = new ConcurrentHashMap<>();
    }
    
    /**
     * 获取单例实例
     * @return UserSessionManager实例
     */
    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }
    
    /**
     * 添加用户会话
     * @param username 用户名
     * @param session WebSocket会话
     */
    public void addUserSession(String username, Session session) {
        activeSessions.put(session.getId(), session);
        usernameToSessionId.put(username, session.getId());
    }
    
    /**
     * 移除用户会话
     * @param username 用户名
     * @return 被移除的会话，如果不存在则返回null
     */
    public Session removeUserSession(String username) {
        String sessionId = usernameToSessionId.remove(username);
        if (sessionId != null) {
            return activeSessions.remove(sessionId);
        }
        return null;
    }
    
    /**
     * 根据会话ID移除用户会话
     * @param sessionId 会话ID
     * @return 被移除的会话，如果不存在则返回null
     */
    public Session removeSessionById(String sessionId) {
        // 移除username到sessionId的映射
        usernameToSessionId.entrySet()
            .removeIf(entry -> entry.getValue().equals(sessionId));
        
        // 移除并返回会话
        return activeSessions.remove(sessionId);
    }
    
    /**
     * 获取用户会话
     * @param username 用户名
     * @return 用户会话，如果不存在则返回null
     */
    public Session getUserSession(String username) {
        String sessionId = usernameToSessionId.get(username);
        if (sessionId != null) {
            return activeSessions.get(sessionId);
        }
        return null;
    }
    
    /**
     * 获取所有活跃会话
     * @return 活跃会话Map
     */
    public Map<String, Session> getAllSessions() {
        return activeSessions;
    }
    
    /**
     * 获取当前活跃用户数量
     * @return 活跃用户数量
     */
    public int getActiveUserCount() {
        return activeSessions.size();
    }
    
    /**
     * 获取用户名列表
     * @return 所有活跃用户名
     */
    public Iterable<String> getUsernames() {
        return usernameToSessionId.keySet();
    }
    
    /**
     * 检查用户是否在线
     * @param username 用户名
     * @return 如果用户在线则返回true
     */
    public boolean isUserOnline(String username) {
        return usernameToSessionId.containsKey(username);
    }
    
    /**
     * 根据会话ID获取用户名
     * @param sessionId 会话ID
     * @return 用户名，如果不存在则返回null
     */
    public String getUsernameBySessionId(String sessionId) {
        for (Map.Entry<String, String> entry : usernameToSessionId.entrySet()) {
            if (entry.getValue().equals(sessionId)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
