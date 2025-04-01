package com.ergouwebchat.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 聊天室实体类
 * <p>表示一个聊天群组，包含成员列表和相关属性</p>
 */
public class ChatRoom {
    // Getters and Setters
    /**
     * 聊天室唯一标识
     */
    @Getter
    @Setter
    private String roomId;

    /**
     * 聊天室名称
     * @required 长度2-30个字符
     */
    @Getter
    @Setter
    @NotBlank
    @Size(min = 2, max = 30)
    private String name;

    /**
     * 聊天室描述
     */
    @Getter
    @Setter
    @Size(max = 200)
    private String description;

    /**
     * 聊天室创建者ID
     */
    @Getter
    @Setter
    @NotBlank
    private String creatorId;

    /**
     * 聊天室创建时间
     */
    @Getter
    @Setter
    private LocalDateTime createdAt;

    /**
     * 聊天室成员ID集合
     */
    @Getter
    @Setter
    private Set<String> memberIds;

    /**
     * 聊天室管理员ID集合
     */
    @Getter
    @Setter
    private Set<String> adminIds;

    /**
     * 是否为私有聊天室
     */
    private boolean isPrivate;

    /**
     * 无参构造方法
     */
    public ChatRoom() {
        this.roomId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.memberIds = new HashSet<>();
        this.adminIds = new HashSet<>();
        this.isPrivate = false;
    }

    /**
     * 基础构造方法
     * @param name 聊天室名称
     * @param creatorId 创建者ID
     */
    public ChatRoom(String name, String creatorId) {
        this();
        this.name = name;
        this.creatorId = creatorId;
        this.adminIds.add(creatorId);
        this.memberIds.add(creatorId);
    }

    /**
     * 全参构造方法
     * @param name 聊天室名称
     * @param description 聊天室描述
     * @param creatorId 创建者ID
     * @param isPrivate 是否为私有聊天室
     */
    public ChatRoom(String name, String description, String creatorId, boolean isPrivate) {
        this(name, creatorId);
        this.description = description;
        this.isPrivate = isPrivate;
    }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    /**
     * 添加成员到聊天室
     * @param userId 用户ID
     * @return 添加是否成功
     */
    public boolean addMember(String userId) {
        return this.memberIds.add(userId);
    }

    /**
     * 从聊天室移除成员
     * @param userId 用户ID
     * @return 移除是否成功
     */
    public boolean removeMember(String userId) {
        if (userId.equals(this.creatorId)) {
            return false; // 创建者不能被移除
        }
        this.adminIds.remove(userId); // 如果是管理员，同时移除管理员身份
        return this.memberIds.remove(userId);
    }

    /**
     * 添加管理员
     * @param userId 用户ID
     * @return 添加是否成功
     */
    public boolean addAdmin(String userId) {
        if (!this.memberIds.contains(userId)) {
            return false; // 必须先是成员才能成为管理员
        }
        return this.adminIds.add(userId);
    }

    /**
     * 移除管理员
     * @param userId 用户ID
     * @return 移除是否成功
     */
    public boolean removeAdmin(String userId) {
        if (userId.equals(this.creatorId)) {
            return false; // 创建者管理员身份不能被移除
        }
        return this.adminIds.remove(userId);
    }

    /**
     * 检查用户是否为聊天室成员
     * @param userId 用户ID
     * @return 是否为成员
     */
    public boolean isMember(String userId) {
        return this.memberIds.contains(userId);
    }

    /**
     * 检查用户是否为聊天室管理员
     * @param userId 用户ID
     * @return 是否为管理员
     */
    public boolean isAdmin(String userId) {
        return this.adminIds.contains(userId);
    }

    /**
     * 获取聊天室成员数量
     * @return 成员数量
     */
    public int getMemberCount() {
        return this.memberIds.size();
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId='" + roomId + '\'' +
                ", name='" + name + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", memberCount=" + getMemberCount() +
                ", isPrivate=" + isPrivate +
                '}';
    }
}
