package com.techanalysis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.techanalysis.dto.AuthResponse;
import com.techanalysis.dto.LoginRequest;
import com.techanalysis.dto.RegisterRequest;
import com.techanalysis.entity.User;
import com.techanalysis.repository.UserMapper;
import com.techanalysis.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername())
        );
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public AuthResponse register(RegisterRequest request) {
        User existing = userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername())
        );
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        userMapper.insert(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    public User getUserByUsername(String username) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    }
}
