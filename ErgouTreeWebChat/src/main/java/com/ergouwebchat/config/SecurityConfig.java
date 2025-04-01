package com.ergouwebchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 安全配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 配置密码加密器
     * @return 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 配置安全过滤链
     * @param http HTTP安全配置
     * @return 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // 禁用CSRF保护，因为我们使用WebSocket
            .authorizeHttpRequests(authorize -> authorize
                // 允许访问静态资源
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // 允许访问首页和登录页
                .requestMatchers("/", "/index", "/login").permitAll()
                // 允许访问WebSocket端点
                .requestMatchers("/ws/**").permitAll()
                // 允许访问聊天页面
                .requestMatchers("/chat").permitAll()
                // 允许访问注册页面和注册处理
                .requestMatchers("/register", "/register/**").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/chat")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }
}
