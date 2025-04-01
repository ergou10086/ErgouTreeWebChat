/**
 * WebSocket测试脚本
 * 用于测试WebSocket连接和消息发送/接收功能
 */
document.addEventListener('DOMContentLoaded', function() {
    const connectBtn = document.getElementById('connectBtn');
    const disconnectBtn = document.getElementById('disconnectBtn');
    const sendBtn = document.getElementById('sendBtn');
    const messageInput = document.getElementById('messageInput');
    const usernameInput = document.getElementById('usernameInput');
    const statusOutput = document.getElementById('statusOutput');
    const messagesOutput = document.getElementById('messagesOutput');
    
    let webSocket = null;
    
    // 连接按钮点击事件
    connectBtn.addEventListener('click', function() {
        const username = usernameInput.value.trim();
        if (!username) {
            appendStatus('错误', '请输入用户名');
            return;
        }
        
        connectWebSocket(username);
    });
    
    // 断开连接按钮点击事件
    disconnectBtn.addEventListener('click', function() {
        if (webSocket) {
            webSocket.close();
            webSocket = null;
            appendStatus('信息', '已断开连接');
            updateButtonState(false);
        }
    });
    
    // 发送消息按钮点击事件
    sendBtn.addEventListener('click', function() {
        const message = messageInput.value.trim();
        if (!message) {
            appendStatus('错误', '请输入消息');
            return;
        }
        
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            const messageObj = {
                type: 'TEXT',
                sender: usernameInput.value.trim(),
                recipient: 'ALL',
                content: message,
                timestamp: new Date().toISOString()
            };
            
            webSocket.send(JSON.stringify(messageObj));
            appendMessage('发送', message);
            messageInput.value = '';
        } else {
            appendStatus('错误', 'WebSocket未连接');
        }
    });
    
    /**
     * 连接WebSocket
     * @param {string} username 用户名
     */
    function connectWebSocket(username) {
        // 获取当前主机
        const host = window.location.host;
        const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1) + 1) || '/';
        
        // 创建WebSocket连接
        const wsUrl = `${wsProtocol}//${host}${contextPath}ws/chat/${username}`;
        appendStatus('信息', `正在连接: ${wsUrl}`);
        
        webSocket = new WebSocket(wsUrl);
        
        // 连接打开事件
        webSocket.onopen = function(event) {
            appendStatus('成功', '已连接到WebSocket服务器');
            updateButtonState(true);
        };
        
        // 接收消息事件
        webSocket.onmessage = function(event) {
            try {
                const message = JSON.parse(event.data);
                appendMessage('接收', `${message.sender}: ${message.content}`);
            } catch (error) {
                appendMessage('接收', event.data);
            }
        };
        
        // 连接关闭事件
        webSocket.onclose = function(event) {
            appendStatus('信息', `WebSocket连接已关闭，代码: ${event.code}`);
            updateButtonState(false);
        };
        
        // 连接错误事件
        webSocket.onerror = function(event) {
            appendStatus('错误', 'WebSocket连接发生错误');
            console.error('WebSocket错误:', event);
        };
    }
    
    /**
     * 添加状态信息
     * @param {string} type 状态类型
     * @param {string} message 状态消息
     */
    function appendStatus(type, message) {
        const statusItem = document.createElement('div');
        statusItem.className = `status-item ${type.toLowerCase()}`;
        statusItem.innerHTML = `<strong>[${type}]</strong> ${message}`;
        statusOutput.appendChild(statusItem);
        statusOutput.scrollTop = statusOutput.scrollHeight;
    }
    
    /**
     * 添加消息
     * @param {string} direction 消息方向（发送/接收）
     * @param {string} message 消息内容
     */
    function appendMessage(direction, message) {
        const messageItem = document.createElement('div');
        messageItem.className = `message-item ${direction.toLowerCase()}`;
        messageItem.innerHTML = `<strong>[${direction}]</strong> ${message}`;
        messagesOutput.appendChild(messageItem);
        messagesOutput.scrollTop = messagesOutput.scrollHeight;
    }
    
    /**
     * 更新按钮状态
     * @param {boolean} connected 是否已连接
     */
    function updateButtonState(connected) {
        connectBtn.disabled = connected;
        disconnectBtn.disabled = !connected;
        sendBtn.disabled = !connected;
        messageInput.disabled = !connected;
    }
    
    // 初始化按钮状态
    updateButtonState(false);
});
