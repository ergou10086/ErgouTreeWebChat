/**
 * 聊天室JavaScript
 * 处理WebSocket连接、消息发送接收和UI交互
 */
document.addEventListener('DOMContentLoaded', function() {
    // DOM元素
    const messageContainer = document.getElementById('messageContainer');
    const messageInput = document.getElementById('messageInput');
    const sendMessageBtn = document.getElementById('sendMessageBtn');
    const userList = document.getElementById('userList');
    const userSearch = document.getElementById('userSearch');
    const connectionStatus = document.getElementById('connectionStatus');
    const currentUser = document.getElementById('currentUser');
    const chatTitle = document.getElementById('chatTitle');
    const onlineCount = document.getElementById('onlineCount');
    const logoutBtn = document.getElementById('logoutBtn');
    const toggleThemeBtn = document.getElementById('toggleThemeBtn');
    const clearChatBtn = document.getElementById('clearChatBtn');
    const uploadImageBtn = document.getElementById('uploadImageBtn');
    const uploadFileBtn = document.getElementById('uploadFileBtn');
    const imageInput = document.getElementById('imageInput');
    const fileInput = document.getElementById('fileInput');
    const emojiBtn = document.getElementById('emojiBtn');
    const emojiPicker = document.getElementById('emojiPicker');
    const notification = document.getElementById('notification');
    const notificationTitle = document.getElementById('notificationTitle');
    const notificationMessage = document.getElementById('notificationMessage');
    const notificationIcon = document.getElementById('notificationIcon');
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const inviteBtn = document.getElementById('inviteBtn');
    const helpBtn = document.getElementById('helpBtn');
    const sharedFiles = document.getElementById('sharedFiles');
    const typingIndicator = document.getElementById('typingIndicator');
    
    // 变量
    let username = '';
    let webSocket = null;
    let currentRecipient = null; // 当前私聊对象
    let darkMode = false;
    let userListData = [];
    let fileUploads = [];
    let sharedFilesList = [];
    let typingUsers = new Set(); // 正在输入的用户集合
    let typingTimer = null; // 输入计时器
    let unreadMessages = {}; // 未读消息计数
    
    // 初始化
    init();
    
    /**
     * 初始化函数
     */
    function init() {
        // 从会话存储获取用户名
        username = sessionStorage.getItem('chatUsername');
        
        // 如果没有用户名，重定向到登录页
        if (!username) {
            window.location.href = '/';
            return;
        }
        
        // 显示当前用户名
        currentUser.textContent = username;
        
        // 连接WebSocket
        connectWebSocket();
        
        // 绑定事件
        bindEvents();
        
        // 初始化表情选择器
        initEmojiPicker();
        
        // 检查暗色模式设置
        checkDarkMode();
    }
    
    /**
     * 连接WebSocket
     */
    function connectWebSocket() {
        // 获取当前主机
        const host = window.location.host;
        const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1) + 1) || '/';
        
        // 创建WebSocket连接
        const wsUrl = `${wsProtocol}//${host}${contextPath}ws/chat/${username}`;
        console.log("正在连接WebSocket: " + wsUrl);
        webSocket = new WebSocket(wsUrl);
        
        // 连接打开事件
        webSocket.onopen = function(event) {
            updateConnectionStatus(true);
            showNotification('连接成功', '已成功连接到聊天服务器', 'success');
            console.log("WebSocket连接已建立");
        };
        
        // 接收消息事件
        webSocket.onmessage = function(event) {
            const message = JSON.parse(event.data);
            processIncomingMessage(message);
        };
        
        // 连接关闭事件
        webSocket.onclose = function(event) {
            updateConnectionStatus(false);
            showNotification('连接断开', '与服务器的连接已断开', 'error');
            
            // 尝试重新连接
            setTimeout(function() {
                if (document.visibilityState !== 'hidden') {
                    connectWebSocket();
                }
            }, 3000);
        };
        
        // 连接错误事件
        webSocket.onerror = function(error) {
            console.error('WebSocket错误:', error);
            updateConnectionStatus(false);
            showNotification('连接错误', '无法连接到聊天服务器', 'error');
        };
    }
    
    /**
     * 绑定事件
     */
    function bindEvents() {
        // 发送消息按钮点击事件
        sendMessageBtn.addEventListener('click', sendMessage);
        
        // 消息输入框按键事件
        messageInput.addEventListener('keydown', function(event) {
            if (event.key === 'Enter' && !event.shiftKey) {
                event.preventDefault();
                sendMessage();
            } else {
                // 发送正在输入的状态
                sendTypingStatus(true);
            }
        });
        
        // 消息输入框失去焦点事件
        messageInput.addEventListener('blur', function() {
            sendTypingStatus(false);
        });
        
        // 用户搜索框输入事件
        userSearch.addEventListener('input', function() {
            filterUserList(this.value);
        });
        
        // 登出按钮点击事件
        logoutBtn.addEventListener('click', function() {
            sessionStorage.removeItem('chatUsername');
            if (webSocket) {
                webSocket.close();
            }
            window.location.href = '/';
        });
        
        // 切换主题按钮点击事件
        toggleThemeBtn.addEventListener('click', toggleDarkMode);
        
        // 清空聊天按钮点击事件
        clearChatBtn.addEventListener('click', function() {
            showConfirmModal('清空聊天记录', '确定要清空当前聊天记录吗？这个操作不可撤销。', function() {
                clearMessages();
            });
        });
        
        // 上传图片按钮点击事件
        uploadImageBtn.addEventListener('click', function() {
            imageInput.click();
        });
        
        // 上传文件按钮点击事件
        uploadFileBtn.addEventListener('click', function() {
            fileInput.click();
        });
        
        // 图片选择事件
        imageInput.addEventListener('change', handleImageUpload);
        
        // 文件选择事件
        fileInput.addEventListener('change', handleFileUpload);
        
        // 表情按钮点击事件
        emojiBtn.addEventListener('click', toggleEmojiPicker);
        
        // 关闭模态框按钮点击事件
        closeModalBtn.addEventListener('click', closeModal);
        
        // 邀请按钮点击事件
        inviteBtn.addEventListener('click', showInviteModal);
        
        // 帮助按钮点击事件
        helpBtn.addEventListener('click', showHelpModal);
        
        // 文档点击事件（关闭表情选择器）
        document.addEventListener('click', function(event) {
            if (!emojiBtn.contains(event.target) && !emojiPicker.contains(event.target)) {
                emojiPicker.classList.add('hidden');
            }
        });
        
        // 窗口可见性改变事件
        document.addEventListener('visibilitychange', function() {
            if (document.visibilityState === 'visible' && (!webSocket || webSocket.readyState !== WebSocket.OPEN)) {
                connectWebSocket();
            }
        });
    }
    
    /**
     * 发送消息
     */
    function sendMessage() {
        const content = messageInput.value.trim();
        
        // 如果消息为空，不发送
        if (!content) {
            return;
        }
        
        // 创建消息对象
        const message = {
            type: 'TEXT',
            sender: username,
            content: content,
            timestamp: new Date().toISOString()
        };
        
        // 如果是私聊，添加接收者
        if (currentRecipient) {
            message.recipient = currentRecipient;
        }
        
        // 生成消息ID
        message.id = generateMessageId();
        
        // 发送消息
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify(message));
            
            // 清空输入框
            messageInput.value = '';
            
            // 聚焦输入框
            messageInput.focus();
        } else {
            showNotification('发送失败', '无法连接到聊天服务器', 'error');
            
            // 尝试重新连接
            connectWebSocket();
        }
    }
    
    /**
     * 处理接收到的消息
     * @param {Object} message 消息对象
     */
    function processIncomingMessage(message) {
        console.log('收到消息:', message);
        
        // 根据消息类型处理
        switch (message.type) {
            case 'TEXT':
                addMessageToChat(message);
                if (message.sender !== username) {
                    sendReadReceipt(message.id);
                }
                break;
                
            case 'IMAGE':
                addImageMessageToChat(message);
                if (message.sender !== username) {
                    sendReadReceipt(message.id);
                }
                break;
                
            case 'FILE':
                addFileMessageToChat(message);
                if (message.sender !== username) {
                    sendReadReceipt(message.id);
                }
                break;
                
            case 'SYSTEM_NOTICE':
                addSystemMessageToChat(message);
                
                // 如果消息包含用户列表信息，更新用户列表
                if (message.metadata && message.metadata.userList) {
                    updateUserList(message.metadata.userList);
                    
                    // 更新在线用户数量
                    if (message.metadata.userCount) {
                        onlineCount.textContent = message.metadata.userCount;
                    }
                }
                break;
                
            case 'USER_JOIN':
                addSystemMessageToChat(message);
                break;
                
            case 'USER_LEAVE':
                addSystemMessageToChat(message);
                break;
                
            case 'TYPING':
                handleTypingStatus(message);
                break;
                
            case 'READ_RECEIPT':
                updateMessageReadStatus(message);
                break;
                
            default:
                console.warn('未知消息类型:', message.type);
        }
        
        // 滚动到底部
        scrollToBottom();
    }
    
    /**
     * 添加文本消息到聊天区域
     * @param {Object} message 消息对象
     */
    function addMessageToChat(message) {
        const isSent = message.sender === username;
        
        // 创建消息元素
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', isSent ? 'self' : 'other', 'mb-4');
        messageElement.setAttribute('data-message-id', message.id || '');
        
        // 消息HTML
        let messageHTML = '';
        
        // 如果是接收的消息，添加头像和用户名
        if (!isSent) {
            // 生成头像颜色（基于用户名）
            const avatarColor = generateAvatarColor(message.sender);
            
            messageHTML += `
                <div class="mr-2 flex flex-col items-center">
                    <div class="user-avatar" style="background-color: ${avatarColor}">
                        ${message.sender.charAt(0).toUpperCase()}
                    </div>
                    <span class="text-xs text-gray-500 mt-1">${message.sender}</span>
                </div>
            `;
        }
        
        // 消息气泡
        messageHTML += `
            <div class="message-bubble ${isSent ? 'sent' : 'received'}">
                ${message.content}
                <div class="text-xs text-gray-500 text-right mt-1">
                    ${formatTimestamp(message.timestamp)}
                </div>
                ${isSent ? `<span class="read-status" data-read="false">
                    <i class="fas fa-check text-gray-400"></i>
                </span>` : ''}
            </div>
        `;
        
        // 设置HTML
        messageElement.innerHTML = messageHTML;
        
        // 添加到消息容器
        messageContainer.appendChild(messageElement);
    }
    
    /**
     * 发送已读回执
     * @param {string} messageId 消息ID
     */
    function sendReadReceipt(messageId) {
        // 如果WebSocket未连接，直接返回
        if (!webSocket || webSocket.readyState !== WebSocket.OPEN || !messageId) {
            return;
        }
        
        // 创建已读回执消息
        const readReceiptMessage = {
            type: 'READ_RECEIPT',
            sender: username,
            recipient: currentRecipient || 'GROUP',
            content: messageId,
            timestamp: new Date().toISOString()
        };
        
        // 发送已读回执
        webSocket.send(JSON.stringify(readReceiptMessage));
    }
    
    /**
     * 更新消息已读状态
     * @param {Object} message 已读回执消息
     */
    function updateMessageReadStatus(message) {
        // 如果不是自己发送的消息的已读回执，忽略
        if (message.content === '' || username !== message.recipient) {
            return;
        }
        
        // 查找对应的消息元素
        const messageElement = document.querySelector(`.message[data-message-id="${message.content}"]`);
        if (!messageElement) {
            return;
        }
        
        // 更新已读状态
        const readStatus = messageElement.querySelector('.read-status');
        if (readStatus) {
            readStatus.setAttribute('data-read', 'true');
            readStatus.innerHTML = '<i class="fas fa-check-double text-blue-500"></i>';
        }
    }
    
    /**
     * 生成消息ID
     * @returns {string} 消息ID
     */
    function generateMessageId() {
        return 'msg_' + Date.now() + '_' + Math.floor(Math.random() * 1000);
    }
    
    /**
     * 切换到私聊
     * @param {string} recipient 接收者用户名
     */
    function switchToPrivateChat(recipient) {
        // 更新当前接收者
        currentRecipient = recipient;
        
        // 更新聊天标题
        chatTitle.textContent = `与 ${recipient} 的私聊`;
        
        // 更新用户列表选中状态
        const userItems = userList.querySelectorAll('.user-list-item');
        userItems.forEach(function(item, index) {
            if (index === 0) {
                item.classList.remove('active');
            } else if (index > 1) {
                const username = item.querySelector('span:not(.ml-auto)').textContent;
                if (username === recipient) {
                    item.classList.add('active');
                } else {
                    item.classList.remove('active');
                }
            }
        });
        
        // 更新聊天信息
        document.getElementById('currentChatInfo').innerHTML = `
            <p><i class="fas fa-user mr-1"></i>与 ${recipient} 的私聊</p>
            <p><i class="fas fa-circle text-green-500 mr-1 text-xs"></i>在线</p>
        `;
        
        // 更新未读消息计数
        if (unreadMessages[recipient]) {
            delete unreadMessages[recipient];
            updateUserList(userListData);
        }
    }
    
    /**
     * 更新用户列表
     * @param {Array} users 用户列表
     */
    function updateUserList(users) {
        // 清空用户列表
        userList.innerHTML = '';
        
        // 添加群聊选项
        const groupChatElement = document.createElement('div');
        groupChatElement.classList.add('user-list-item', !currentRecipient ? 'active' : '');
        groupChatElement.innerHTML = `
            <div class="flex items-center">
                <i class="fas fa-users mr-2 text-blue-500"></i>
                <span>群聊</span>
                <span class="ml-auto bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full">${users.length}</span>
                ${unreadMessages['GROUP'] ? `<div class="unread-badge">${unreadMessages['GROUP']}</div>` : ''}
            </div>
        `;
        
        // 点击切换到群聊
        groupChatElement.addEventListener('click', function() {
            switchToGroupChat();
        });
        
        // 添加到用户列表
        userList.appendChild(groupChatElement);
        
        // 添加分隔线
        const divider = document.createElement('div');
        divider.classList.add('border-t', 'my-2');
        userList.appendChild(divider);
        
        // 添加用户
        users.forEach(function(user) {
            // 跳过当前用户
            if (user === username) {
                return;
            }
            
            // 生成头像颜色（基于用户名）
            const avatarColor = generateAvatarColor(user);
            
            // 创建用户列表项
            const userElement = document.createElement('div');
            userElement.classList.add('user-list-item', currentRecipient === user ? 'active' : '');
            
            // 用户HTML
            userElement.innerHTML = `
                <div class="flex items-center">
                    <div class="user-status online"></div>
                    <div class="user-avatar" style="background-color: ${avatarColor}; width: 24px; height: 24px; font-size: 12px;">
                        ${user.charAt(0).toUpperCase()}
                    </div>
                    <span class="ml-2">${user}</span>
                    ${unreadMessages[user] ? `<div class="unread-badge">${unreadMessages[user]}</div>` : ''}
                </div>
            `;
            
            // 点击切换到私聊
            userElement.addEventListener('click', function() {
                switchToPrivateChat(user);
            });
            
            // 添加到用户列表
            userList.appendChild(userElement);
        });
    }
});
