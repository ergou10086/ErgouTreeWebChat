package com.ergouwebchat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web页面控制器
 * <p>处理页面路由</p>
 */
@Controller
public class WebController {

    /**
     * 首页路由
     * @return 首页视图名
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    /**
     * 聊天室路由
     * @return 聊天室视图名
     */
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}
