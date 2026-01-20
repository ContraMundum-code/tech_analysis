package com.techanalysis.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techanalysis.entity.User;
import com.techanalysis.repository.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器 - 应用启动时自动创建默认用户
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createDefaultAdmin();
        createDefaultUser();
    }

    private void createDefaultAdmin() {
        User existing = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, "admin")
        );
        if (existing == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");
            userMapper.insert(admin);
            log.info("默认管理员账号已创建: admin / admin123");
        } else {
            // 更新密码确保正确
            existing.setPasswordHash(passwordEncoder.encode("admin123"));
            userMapper.updateById(existing);
            log.info("管理员账号密码已更新: admin / admin123");
        }
    }

    private void createDefaultUser() {
        User existing = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, "user")
        );
        if (existing == null) {
            User user = new User();
            user.setUsername("user");
            user.setPasswordHash(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setRole("USER");
            userMapper.insert(user);
            log.info("默认用户账号已创建: user / user123");
        } else {
            existing.setPasswordHash(passwordEncoder.encode("user123"));
            userMapper.updateById(existing);
            log.info("用户账号密码已更新: user / user123");
        }
    }
}
