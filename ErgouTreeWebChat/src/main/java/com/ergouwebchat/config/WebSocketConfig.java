package com.ergouwebchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 * <p>启用WebSocket支持</p>
 */
@Configuration
public class WebSocketConfig {
    
    /**
     * 注册WebSocket端点
     * @return ServerEndpointExporter bean
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
