package com.ergouwebchat.controller;

import com.ergouwebchat.model.Message;
import com.ergouwebchat.service.MessageService;
import com.ergouwebchat.service.UserSessionManager;
import com.ergouwebchat.util.JsonUtils;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WebSocket端点类
 * <p>处理WebSocket连接、消息接收和发送</p>
 */
@ServerEndpoint("/ws/chat/{username}")
public class ChatWebSocketEndpoint {
    private static final Logger LOGGER = Logger.getLogger(ChatWebSocketEndpoint.class.getName());
    
    // 使用单例模式的用户会话管理器
    private final UserSessionManager sessionManager = UserSessionManager.getInstance();
    
    // 消息处理服务
    private final MessageService messageService = new MessageService();
    
    // 当前连接的用户名
    private String username;
    
    /**
     * 处理WebSocket连接建立事件
     * @param session WebSocket会话
     * @param username 用户名路径参数
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        LOGGER.info("WebSocket连接已建立: " + username);
        
        // 存储用户会话
        this.username = username;
        sessionManager.addUserSession(username, session);
        
        // 创建并广播用户加入消息
        Message joinMessage = messageService.createUserJoinMessage(username);
        messageService.process(joinMessage);
        
        // 设置会话属性
        session.getUserProperties().put("username", username);
    }
    
    /**
     * 处理WebSocket连接关闭事件
     * @param session WebSocket会话
     */
    @OnClose
    public void onClose(Session session) {
        // 获取用户名
        String closingUsername = (String) session.getUserProperties().get("username");
        if (closingUsername == null) {
            closingUsername = this.username;
        }
        
        LOGGER.info("WebSocket连接已关闭: " + closingUsername);
        
        // 移除用户会话
        sessionManager.removeUserSession(closingUsername);
        
        // 创建并广播用户离开消息
        Message leaveMessage = messageService.createUserLeaveMessage(closingUsername);
        messageService.process(leaveMessage);
    }
    
    /**
     * 处理WebSocket消息接收事件
     * @param session WebSocket会话
     * @param message 接收到的消息文本
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        String senderUsername = (String) session.getUserProperties().get("username");
        LOGGER.info("收到来自 " + senderUsername + " 的消息: " + message);
        
        // 处理接收到的消息
        messageService.processIncomingMessage(message, session);
    }
    
    /**
     * 处理WebSocket错误事件
     * @param session WebSocket会话
     * @param throwable 错误信息
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        String errorUsername = (String) session.getUserProperties().get("username");
        LOGGER.log(Level.SEVERE, "WebSocket错误 (" + errorUsername + "): " + throwable.getMessage(), throwable);
        
        try {
            // 发送错误消息给客户端
            Message errorMessage = new Message();
            errorMessage.setSender("SYSTEM");
            errorMessage.setContent("发生错误: " + throwable.getMessage());
            
            String jsonError = JsonUtils.toJson(errorMessage);
            session.getBasicRemote().sendText(jsonError);
            
            // 关闭连接
            session.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "发送错误消息失败: " + e.getMessage(), e);
        }
        
        // 移除用户会话
        if (errorUsername != null) {
            sessionManager.removeUserSession(errorUsername);
            
            // 广播用户离开消息
            Message leaveMessage = messageService.createUserLeaveMessage(errorUsername);
            messageService.process(leaveMessage);
        }
    }
}
