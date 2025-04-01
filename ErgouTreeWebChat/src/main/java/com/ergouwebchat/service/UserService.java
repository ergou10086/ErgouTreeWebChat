package com.ergouwebchat.service;

import com.ergouwebchat.model.entity.User;
import com.ergouwebchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 创建新用户
     * @param username 用户名
     * @param password 密码
     * @return 创建的用户
     */
    @Transactional
    public User createUser(String username, String password) {
        // 确保用户名已trim
        username = username.trim();
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password.trim())); // 确保密码trim后加密
        user.setStatus(User.UserStatus.OFFLINE);
        user.setCreatedAt(LocalDateTime.now());
        
        // 生成随机头像颜色
        user.setAvatarColor(generateRandomColor());
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户
     */
    @Transactional
    public User login(String username, String password) {
        username = username.trim(); // 新增trim处理
        
        Optional<User> optionalUser = userRepository.findByUsername(username);
        
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = optionalUser.get();
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 更新用户状态和登录时间
        user.setStatus(User.UserStatus.ONLINE);
        user.setLastLogin(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * 用户登出
     * @param username 用户名
     */
    @Transactional
    public void logout(String username) {
        // 查找用户
        Optional<User> optionalUser = userRepository.findByUsername(username);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 更新用户状态
            user.setStatus(User.UserStatus.OFFLINE);
            userRepository.save(user);
        }
    }
    
    /**
     * 获取所有在线用户
     * @return 在线用户列表
     */
    public List<User> getOnlineUsers() {
        return userRepository.findByStatus(User.UserStatus.ONLINE);
    }
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 生成随机颜色
     * @return 十六进制颜色代码
     */
    private String generateRandomColor() {
        // 预定义的颜色列表
        String[] colors = {
            "#4CAF50", "#2196F3", "#9C27B0", "#F44336", "#FF9800",
            "#009688", "#673AB7", "#3F51B5", "#E91E63", "#FFC107"
        };
        
        // 随机选择一个颜色
        int index = (int) (Math.random() * colors.length);
        return colors[index];
    }
}
