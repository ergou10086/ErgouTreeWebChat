package com.ergouwebchat.model;


import com.ergouwebchat.model.enums.UserStatus;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户实体类
 * <p>包含用户唯一标识、基础信息、在线状态等核心属性</p>
 */
public class User {
    // getters and setters
    /**
     * 用户唯一标识（UUID格式）
     * @required 必须符合RFC4122标准格式
     */
    @Getter
    @Setter
    @NotBlank
    private String userId;

    /**
     * 用户显示名称
     * @required 长度2-20个字符，支持中文/数字/字母组合
     */
    @Setter
    @Getter
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    /**
     * 用户在线状态
     * @see UserStatus
     * @required 必须指定有效状态值
     */
    @Getter
    @NotNull
    private UserStatus status;

    /**
     * 用户头像URL
     */
    @Setter
    @Getter
    private String avatarUrl;

    /**
     * 用户最后活跃时间
     */
    @Setter
    @Getter
    private LocalDateTime lastActiveTime;

    /**
     * 用户是否为管理员
     */
    private boolean isAdmin;

    /**
     * 无参构造方法
     */
    public User() {
        // 默认构造方法
    }

    /**
     * 基础构造方法
     * @param userId 用户唯一标识（非空）
     * @param username 显示名称（长度2-20字符）
     */
    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.status = UserStatus.OFFLINE;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 全参构造方法
     * @param userId 用户唯一标识（非空）
     * @param username 显示名称（长度2-20字符）
     * @param status 用户状态枚举
     * @throws IllegalArgumentException 当参数不符合校验规则时抛出
     */
    public User(String userId, String username, UserStatus status) {
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.lastActiveTime = LocalDateTime.now();
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        if (status == UserStatus.ONLINE) {
            this.lastActiveTime = LocalDateTime.now();
        }
    }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    /**
     * 更新用户活跃时间为当前时间
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", lastActiveTime=" + lastActiveTime +
                '}';
    }
}
