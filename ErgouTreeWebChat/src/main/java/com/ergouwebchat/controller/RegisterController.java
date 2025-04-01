package com.ergouwebchat.controller;

import com.ergouwebchat.model.entity.User;
import com.ergouwebchat.service.UserService;
import com.ergouwebchat.util.MessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 用户注册控制器
 */
@Controller
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 显示注册页面
     * @return 注册页面视图
     */
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    /**
     * 处理注册请求
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param redirectAttributes 重定向属性
     * @return 重定向视图
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes) {
        
        // 新增trim处理和格式验证
        username = username != null ? username.trim() : "";
        password = password != null ? password.trim() : "";
        confirmPassword = confirmPassword != null ? confirmPassword.trim() : "";
        
        if (username.isEmpty()) {
            redirectAttributes.addAttribute("error", "用户名不能为空");
            return "redirect:/register";
        }
        
        // 添加用户名格式验证
        if (!MessageValidator.isValidUsername(username)) {
            redirectAttributes.addAttribute("error", "用户名格式无效（3-20位字母、数字或下划线）");
            return "redirect:/register";
        }
        
        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addAttribute("error", "密码不能为空");
            return "redirect:/register";
        }
        
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addAttribute("error", "两次输入的密码不一致");
            return "redirect:/register";
        }
        
        try {
            // 创建用户
            User user = userService.createUser(username, password);
            
            // 注册成功
            redirectAttributes.addAttribute("success", true);
            return "redirect:/register";
        } catch (Exception e) {
            // 注册失败
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
