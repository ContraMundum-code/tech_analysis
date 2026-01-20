package com.techanalysis.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具 - 运行 main 方法生成密码哈希
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        System.out.println("admin123 hash: " + encoder.encode(adminPassword));
        System.out.println("user123 hash: " + encoder.encode(userPassword));
    }
}
