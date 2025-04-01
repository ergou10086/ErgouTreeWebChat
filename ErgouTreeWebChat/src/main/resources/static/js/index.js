/**
 * 首页JavaScript
 * 处理用户登录和表单验证
 */
document.addEventListener('DOMContentLoaded', function() {
    const joinForm = document.getElementById('joinForm');
    const usernameInput = document.getElementById('username');
    const usernameError = document.getElementById('usernameError');
    
    // 用户名验证规则（字母、数字、下划线，长度3-20）
    const usernamePattern = /^[a-zA-Z0-9_]{3,20}$/;
    
    // 表单提交事件
    joinForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        const username = usernameInput.value.trim();
        
        // 验证用户名
        if (!usernamePattern.test(username)) {
            usernameError.classList.remove('hidden');
            usernameInput.classList.add('border-red-500');
            return;
        }
        
        // 隐藏错误信息
        usernameError.classList.add('hidden');
        usernameInput.classList.remove('border-red-500');
        
        // 保存用户名到会话存储
        sessionStorage.setItem('chatUsername', username);
        
        // 跳转到聊天页面
        window.location.href = '/chat';
    });
    
    // 输入框输入事件
    usernameInput.addEventListener('input', function() {
        if (usernamePattern.test(this.value.trim())) {
            usernameError.classList.add('hidden');
            usernameInput.classList.remove('border-red-500');
        }
    });
    
    // 检查是否已经登录
    const savedUsername = sessionStorage.getItem('chatUsername');
    if (savedUsername) {
        // 如果已经登录，可以自动填充用户名
        usernameInput.value = savedUsername;
    }
});
