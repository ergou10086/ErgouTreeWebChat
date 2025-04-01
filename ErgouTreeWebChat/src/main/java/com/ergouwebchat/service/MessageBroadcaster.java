package com.ergouwebchat.service;

import com.ergouwebchat.model.Message;
import com.ergouwebchat.util.JsonUtils;

import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 消息广播器
 * <p>负责将消息发送给一个或多个用户</p>
 */
public class MessageBroadcaster {
    private static final Logger LOGGER = Logger.getLogger(MessageBroadcaster.class.getName());
    
    private final UserSessionManager sessionManager;
    
    /**
     * 构造函数
     */
    public MessageBroadcaster() {
        this.sessionManager = UserSessionManager.getInstance();
    }
    
    /**
     * 向所有连接的用户广播消息
     * @param message 要广播的消息
     */
    public void broadcastToAll(Message message) {
        String jsonMessage = JsonUtils.toJson(message);
        Map<String, Session> sessions = sessionManager.getAllSessions();
        
        for (Session session : sessions.values()) {
            sendMessageToSession(session, jsonMessage);
        }
    }
    
    /**
     * 向特定用户发送消息
     * @param username 目标用户名
     * @param message 要发送的消息
     * @return 是否发送成功
     */
    public boolean sendToUser(String username, Message message) {
        Session session = sessionManager.getUserSession(username);
        if (session != null) {
            String jsonMessage = JsonUtils.toJson(message);
            return sendMessageToSession(session, jsonMessage);
        }
        return false;
    }
    
    /**
     * 向除特定用户外的所有用户广播消息
     * @param excludeUsername 要排除的用户名
     * @param message 要广播的消息
     */
    public void broadcastToAllExcept(String excludeUsername, Message message) {
        String jsonMessage = JsonUtils.toJson(message);
        Map<String, Session> sessions = sessionManager.getAllSessions();
        
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            String sessionId = entry.getKey();
            String username = sessionManager.getUsernameBySessionId(sessionId);
            
            if (username == null || !username.equals(excludeUsername)) {
                sendMessageToSession(entry.getValue(), jsonMessage);
            }
        }
    }
    
    /**
     * 向除特定用户外的所有用户广播消息
     * @param message 要广播的消息
     * @param excludeUsername 要排除的用户名
     * @return 是否至少有一个用户接收到消息
     */
    public boolean broadcastToAllExcept(Message message, String excludeUsername) {
        String jsonMessage = JsonUtils.toJson(message);
        Map<String, Session> sessions = sessionManager.getAllSessions();
        boolean atLeastOneSent = false;
        
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            String username = sessionManager.getUsernameBySessionId(entry.getKey());
            
            if (username == null || !username.equals(excludeUsername)) {
                boolean sent = sendMessageToSession(entry.getValue(), jsonMessage);
                if (sent) {
                    atLeastOneSent = true;
                }
            }
        }
        
        return atLeastOneSent;
    }
    
    /**
     * 向指定会话发送消息
     * @param session WebSocket会话
     * @param jsonMessage JSON格式的消息
     * @return 是否发送成功
     */
    private boolean sendMessageToSession(Session session, String jsonMessage) {
        try {
            if (session.isOpen()) {
                synchronized (session) {
                    session.getBasicRemote().sendText(jsonMessage);
                }
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "发送消息失败: " + e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * 向多个用户发送消息
     * @param usernames 目标用户名列表
     * @param message 要发送的消息
     * @return 成功发送的用户数量
     */
    public int sendToUsers(Iterable<String> usernames, Message message) {
        int successCount = 0;
        for (String username : usernames) {
            if (sendToUser(username, message)) {
                successCount++;
            }
        }
        return successCount;
    }
}
