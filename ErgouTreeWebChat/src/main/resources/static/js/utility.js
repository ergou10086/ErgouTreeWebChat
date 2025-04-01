/**
 * 工具函数集合
 */

/**
 * 格式化时间戳
 * @param {string} timestamp ISO格式的时间戳
 * @return {string} 格式化后的时间字符串
 */
function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    const now = new Date();
    const isToday = date.toDateString() === now.toDateString();
    
    // 时间格式化选项
    const timeOptions = { hour: '2-digit', minute: '2-digit' };
    const dateOptions = { month: 'short', day: 'numeric' };
    
    // 如果是今天，只显示时间；否则显示日期和时间
    if (isToday) {
        return date.toLocaleTimeString(undefined, timeOptions);
    } else {
        return date.toLocaleDateString(undefined, dateOptions) + ' ' + 
               date.toLocaleTimeString(undefined, timeOptions);
    }
}

/**
 * 生成基于用户名的头像颜色
 * @param {string} username 用户名
 * @return {string} 颜色代码
 */
function generateAvatarColor(username) {
    // 预定义的颜色列表
    const colors = [
        '#4CAF50', '#2196F3', '#9C27B0', '#F44336', '#FF9800',
        '#009688', '#673AB7', '#3F51B5', '#E91E63', '#FFC107'
    ];
    
    // 基于用户名生成索引
    let hash = 0;
    for (let i = 0; i < username.length; i++) {
        hash = username.charCodeAt(i) + ((hash << 5) - hash);
    }
    
    // 取模得到颜色索引
    const index = Math.abs(hash) % colors.length;
    return colors[index];
}

/**
 * 根据文件名获取文件图标
 * @param {string} fileName 文件名
 * @return {string} 图标类名
 */
function getFileIcon(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    
    // 根据文件扩展名返回对应的图标
    switch (extension) {
        case 'pdf':
            return 'fas fa-file-pdf text-red-500';
        case 'doc':
        case 'docx':
            return 'fas fa-file-word text-blue-500';
        case 'xls':
        case 'xlsx':
            return 'fas fa-file-excel text-green-500';
        case 'ppt':
        case 'pptx':
            return 'fas fa-file-powerpoint text-orange-500';
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif':
        case 'bmp':
            return 'fas fa-file-image text-purple-500';
        case 'zip':
        case 'rar':
        case '7z':
            return 'fas fa-file-archive text-yellow-600';
        case 'txt':
            return 'fas fa-file-alt text-gray-500';
        case 'mp3':
        case 'wav':
        case 'ogg':
            return 'fas fa-file-audio text-blue-400';
        case 'mp4':
        case 'avi':
        case 'mov':
            return 'fas fa-file-video text-red-400';
        case 'html':
        case 'css':
        case 'js':
            return 'fas fa-file-code text-indigo-500';
        default:
            return 'fas fa-file text-gray-500';
    }
}

/**
 * 格式化文件大小
 * @param {number} bytes 文件大小（字节）
 * @return {string} 格式化后的文件大小
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * 滚动到消息容器底部
 */
function scrollToBottom() {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

/**
 * 播放通知声音
 */
function playNotificationSound() {
    // 创建音频元素
    const audio = new Audio('/sounds/notification.mp3');
    audio.volume = 0.5;
    audio.play().catch(error => {
        console.log('无法播放通知声音:', error);
    });
}

/**
 * 显示通知
 * @param {string} title 通知标题
 * @param {string} message 通知内容
 * @param {string} type 通知类型（success, error, info, warning）
 */
function showNotification(title, message, type = 'info') {
    const notification = document.getElementById('notification');
    const notificationTitle = document.getElementById('notificationTitle');
    const notificationMessage = document.getElementById('notificationMessage');
    const notificationIcon = document.getElementById('notificationIcon');
    
    // 设置通知内容
    notificationTitle.textContent = title;
    notificationMessage.textContent = message;
    
    // 根据类型设置图标和颜色
    switch (type) {
        case 'success':
            notificationIcon.className = 'fas fa-check-circle text-green-500 text-xl';
            break;
        case 'error':
            notificationIcon.className = 'fas fa-times-circle text-red-500 text-xl';
            break;
        case 'warning':
            notificationIcon.className = 'fas fa-exclamation-circle text-yellow-500 text-xl';
            break;
        case 'info':
        default:
            notificationIcon.className = 'fas fa-info-circle text-blue-500 text-xl';
            break;
    }
    
    // 显示通知
    notification.classList.remove('translate-x-full');
    
    // 3秒后隐藏通知
    setTimeout(function() {
        notification.classList.add('translate-x-full');
    }, 3000);
}

/**
 * 更新连接状态
 * @param {boolean} isConnected 是否已连接
 */
function updateConnectionStatus(isConnected) {
    const connectionStatus = document.getElementById('connectionStatus');
    
    if (isConnected) {
        connectionStatus.innerHTML = '<i class="fas fa-circle text-green-500 mr-1"></i>已连接';
        connectionStatus.className = 'ml-4 px-2 py-1 bg-green-100 text-green-800 text-xs font-medium rounded-full';
    } else {
        connectionStatus.innerHTML = '<i class="fas fa-circle text-red-500 mr-1"></i>已断开';
        connectionStatus.className = 'ml-4 px-2 py-1 bg-red-100 text-red-800 text-xs font-medium rounded-full';
    }
}

/**
 * 切换暗色模式
 */
function toggleDarkMode() {
    const body = document.body;
    const toggleThemeBtn = document.getElementById('toggleThemeBtn');
    const themeIcon = toggleThemeBtn.querySelector('i');
    
    // 切换暗色模式
    body.classList.toggle('dark-theme');
    
    // 更新图标
    if (body.classList.contains('dark-theme')) {
        themeIcon.className = 'fas fa-sun text-yellow-400';
        localStorage.setItem('darkMode', 'true');
    } else {
        themeIcon.className = 'fas fa-moon text-gray-600';
        localStorage.setItem('darkMode', 'false');
    }
}

/**
 * 检查暗色模式设置
 */
function checkDarkMode() {
    const darkMode = localStorage.getItem('darkMode');
    
    if (darkMode === 'true') {
        document.body.classList.add('dark-theme');
        document.getElementById('toggleThemeBtn').querySelector('i').className = 'fas fa-sun text-yellow-400';
    }
}

/**
 * 显示确认模态框
 * @param {string} title 标题
 * @param {string} message 消息内容
 * @param {Function} confirmCallback 确认回调函数
 */
function showConfirmModal(title, message, confirmCallback) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    
    // 设置模态框内容
    modalTitle.textContent = title;
    
    // 创建确认内容
    modalContent.innerHTML = `
        <p class="mb-4">${message}</p>
        <div class="flex justify-end space-x-2">
            <button id="cancelBtn" class="px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 transition-colors">
                取消
            </button>
            <button id="confirmBtn" class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors">
                确认
            </button>
        </div>
    `;
    
    // 绑定按钮事件
    document.getElementById('cancelBtn').addEventListener('click', closeModal);
    document.getElementById('confirmBtn').addEventListener('click', function() {
        confirmCallback();
        closeModal();
    });
    
    // 显示模态框
    modal.classList.remove('hidden');
}

/**
 * 关闭模态框
 */
function closeModal() {
    document.getElementById('modal').classList.add('hidden');
}

/**
 * 清空消息
 */
function clearMessages() {
    const messageContainer = document.getElementById('messageContainer');
    
    // 清空消息容器
    messageContainer.innerHTML = `
        <div class="text-center text-gray-500 my-4">
            <p>聊天记录已清空</p>
            <p>开始新的对话吧！</p>
        </div>
    `;
}

/**
 * 显示邀请模态框
 */
function showInviteModal() {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    
    // 获取当前URL
    const currentUrl = window.location.origin;
    const inviteUrl = currentUrl + '/?invite=true';
    
    // 设置模态框内容
    modalTitle.textContent = '邀请好友';
    
    // 创建邀请内容
    modalContent.innerHTML = `
        <p class="mb-4">分享以下链接邀请好友加入聊天：</p>
        <div class="flex mb-4">
            <input type="text" id="inviteUrl" value="${inviteUrl}" readonly
                class="flex-1 px-4 py-2 border border-gray-300 rounded-l-md focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
            <button id="copyBtn" class="px-4 py-2 bg-blue-500 text-white rounded-r-md hover:bg-blue-600 transition-colors">
                复制
            </button>
        </div>
        <p class="text-sm text-gray-600">
            好友打开链接后，输入用户名即可加入聊天室。
        </p>
    `;
    
    // 绑定复制按钮事件
    document.getElementById('copyBtn').addEventListener('click', function() {
        const inviteUrl = document.getElementById('inviteUrl');
        inviteUrl.select();
        document.execCommand('copy');
        this.textContent = '已复制';
        setTimeout(() => {
            this.textContent = '复制';
        }, 2000);
    });
    
    // 显示模态框
    modal.classList.remove('hidden');
}

/**
 * 显示帮助模态框
 */
function showHelpModal() {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    
    // 设置模态框内容
    modalTitle.textContent = '帮助指南';
    
    // 创建帮助内容
    modalContent.innerHTML = `
        <div class="space-y-4">
            <div>
                <h3 class="font-medium text-gray-800 mb-2">基本功能</h3>
                <ul class="list-disc pl-5 text-gray-700 space-y-1">
                    <li>发送消息：在输入框中输入内容，按回车键或点击发送按钮</li>
                    <li>私聊：点击左侧用户列表中的用户名</li>
                    <li>群聊：点击左侧用户列表中的"群聊"选项</li>
                    <li>退出聊天：点击右上角的"退出"按钮</li>
                </ul>
            </div>
            
            <div>
                <h3 class="font-medium text-gray-800 mb-2">高级功能</h3>
                <ul class="list-disc pl-5 text-gray-700 space-y-1">
                    <li>发送图片：点击输入框左侧的图片图标</li>
                    <li>发送文件：点击输入框左侧的文件图标</li>
                    <li>使用表情：点击输入框左侧的表情图标</li>
                    <li>清空聊天记录：点击聊天标题右侧的清空图标</li>
                    <li>切换主题：点击聊天标题右侧的主题图标</li>
                </ul>
            </div>
            
            <div>
                <h3 class="font-medium text-gray-800 mb-2">快捷键</h3>
                <ul class="list-disc pl-5 text-gray-700 space-y-1">
                    <li><span class="font-mono bg-gray-100 px-1 rounded">Enter</span>：发送消息</li>
                    <li><span class="font-mono bg-gray-100 px-1 rounded">Shift + Enter</span>：换行</li>
                    <li><span class="font-mono bg-gray-100 px-1 rounded">Esc</span>：关闭弹窗</li>
                </ul>
            </div>
        </div>
    `;
    
    // 显示模态框
    modal.classList.remove('hidden');
}

/**
 * 处理图片上传
 * @param {Event} event 事件对象
 */
function handleImageUpload(event) {
    const file = event.target.files[0];
    
    // 如果没有选择文件，直接返回
    if (!file) {
        return;
    }
    
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
        showNotification('上传失败', '请选择图片文件', 'error');
        return;
    }
    
    // 检查文件大小（最大5MB）
    if (file.size > 5 * 1024 * 1024) {
        showNotification('上传失败', '图片大小不能超过5MB', 'error');
        return;
    }
    
    // 创建FormData对象
    const formData = new FormData();
    formData.append('image', file);
    
    // 显示上传中通知
    showNotification('上传中', '正在上传图片，请稍候...', 'info');
    
    // 模拟上传过程（实际项目中应替换为真实的上传API）
    setTimeout(function() {
        // 模拟上传成功
        const imageUrl = URL.createObjectURL(file);
        
        // 创建图片消息
        const message = {
            type: 'IMAGE',
            sender: username,
            content: '',
            timestamp: new Date().toISOString(),
            metadata: {
                imageUrl: imageUrl
            }
        };
        
        // 如果是私聊，添加接收者
        if (currentRecipient) {
            message.recipient = currentRecipient;
        }
        
        // 发送消息
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify(message));
            showNotification('上传成功', '图片已发送', 'success');
        } else {
            showNotification('发送失败', '无法连接到聊天服务器', 'error');
        }
        
        // 清空文件输入
        event.target.value = '';
    }, 1000);
}

/**
 * 处理文件上传
 * @param {Event} event 事件对象
 */
function handleFileUpload(event) {
    const file = event.target.files[0];
    
    // 如果没有选择文件，直接返回
    if (!file) {
        return;
    }
    
    // 检查文件大小（最大20MB）
    if (file.size > 20 * 1024 * 1024) {
        showNotification('上传失败', '文件大小不能超过20MB', 'error');
        return;
    }
    
    // 创建FormData对象
    const formData = new FormData();
    formData.append('file', file);
    
    // 显示上传中通知
    showNotification('上传中', '正在上传文件，请稍候...', 'info');
    
    // 模拟上传过程（实际项目中应替换为真实的上传API）
    setTimeout(function() {
        // 模拟上传成功
        const fileUrl = URL.createObjectURL(file);
        
        // 创建文件消息
        const message = {
            type: 'FILE',
            sender: username,
            content: '分享了文件: ' + file.name,
            timestamp: new Date().toISOString(),
            metadata: {
                fileUrl: fileUrl,
                fileName: file.name,
                fileSize: file.size
            }
        };
        
        // 如果是私聊，添加接收者
        if (currentRecipient) {
            message.recipient = currentRecipient;
        }
        
        // 发送消息
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(JSON.stringify(message));
            showNotification('上传成功', '文件已发送', 'success');
        } else {
            showNotification('发送失败', '无法连接到聊天服务器', 'error');
        }
        
        // 清空文件输入
        event.target.value = '';
    }, 1500);
}

/**
 * 添加到共享文件列表
 * @param {Object} message 文件消息对象
 */
function addToSharedFiles(message) {
    const sharedFiles = document.getElementById('sharedFiles');
    
    // 如果是第一个文件，清空"暂无共享文件"提示
    if (sharedFilesList.length === 0) {
        sharedFiles.innerHTML = '';
    }
    
    // 添加到共享文件列表
    sharedFilesList.push(message);
    
    // 文件图标
    const fileIcon = getFileIcon(message.metadata.fileName);
    
    // 创建文件元素
    const fileElement = document.createElement('div');
    fileElement.className = 'mb-2 p-2 bg-gray-50 rounded hover:bg-gray-100 transition-colors';
    
    // 文件HTML
    fileElement.innerHTML = `
        <div class="flex items-center">
            <i class="${fileIcon} mr-2"></i>
            <div class="overflow-hidden">
                <div class="font-medium text-sm truncate" title="${message.metadata.fileName}">
                    ${message.metadata.fileName}
                </div>
                <div class="text-xs text-gray-500 flex items-center">
                    <span>${formatFileSize(message.metadata.fileSize)}</span>
                    <span class="mx-1">•</span>
                    <span>${message.sender}</span>
                </div>
            </div>
        </div>
        <div class="mt-1">
            <a href="${message.metadata.fileUrl}" target="_blank" class="text-xs text-blue-500 hover:underline">
                <i class="fas fa-download mr-1"></i> 下载
            </a>
        </div>
    `;
    
    // 添加到共享文件列表
    sharedFiles.appendChild(fileElement);
}

/**
 * 初始化表情选择器
 */
function initEmojiPicker() {
    const emojiPicker = document.getElementById('emojiPicker');
    
    // 表情列表
    const emojis = [
        '😀', '😃', '😄', '😁', '😆', '😅', '😂', '🤣', '😊', '😇',
        '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘', '😗', '😙', '😚',
        '😋', '😛', '😝', '😜', '🤪', '🤨', '🧐', '🤓', '😎', '🤩',
        '🥳', '😏', '😒', '😞', '😔', '😟', '😕', '🙁', '☹️', '😣',
        '😖', '😫', '😩', '🥺', '😢', '😭', '😤', '😠', '😡', '🤬',
        '🤯', '😳', '🥵', '🥶', '😱', '😨', '😰', '😥', '😓', '🤗',
        '🤔', '🤭', '🤫', '🤥', '😶', '😐', '😑', '😬', '🙄', '😯',
        '😦', '😧', '😮', '😲', '🥱', '😴', '🤤', '😪', '😵', '🤐'
    ];
    
    // 创建表情元素
    emojis.forEach(function(emoji) {
        const emojiElement = document.createElement('div');
        emojiElement.className = 'emoji-item';
        emojiElement.textContent = emoji;
        
        // 点击表情事件
        emojiElement.addEventListener('click', function() {
            // 在光标位置插入表情
            const messageInput = document.getElementById('messageInput');
            const start = messageInput.selectionStart;
            const end = messageInput.selectionEnd;
            const text = messageInput.value;
            
            messageInput.value = text.substring(0, start) + emoji + text.substring(end);
            
            // 更新光标位置
            messageInput.selectionStart = messageInput.selectionEnd = start + emoji.length;
            
            // 聚焦输入框
            messageInput.focus();
        });
        
        // 添加到表情选择器
        emojiPicker.appendChild(emojiElement);
    });
}

/**
 * 切换表情选择器显示状态
 */
function toggleEmojiPicker() {
    const emojiPicker = document.getElementById('emojiPicker');
    const emojiBtn = document.getElementById('emojiBtn');
    
    // 计算位置
    const rect = emojiBtn.getBoundingClientRect();
    emojiPicker.style.top = (rect.bottom + 5) + 'px';
    emojiPicker.style.left = rect.left + 'px';
    
    // 切换显示状态
    emojiPicker.classList.toggle('hidden');
}

/**
 * 打开图片查看器
 * @param {string} imageUrl 图片URL
 */
function openImageViewer(imageUrl) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalContent = document.getElementById('modalContent');
    
    // 设置模态框内容
    modalTitle.textContent = '图片查看';
    
    // 创建图片查看器
    modalContent.innerHTML = `
        <div class="flex justify-center">
            <img src="${imageUrl}" alt="图片" class="max-w-full max-h-[70vh]">
        </div>
    `;
    
    // 显示模态框
    modal.classList.remove('hidden');
}
