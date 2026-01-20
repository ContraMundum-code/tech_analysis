package com.techanalysis.controller;

import com.techanalysis.dto.*;
import com.techanalysis.entity.User;
import com.techanalysis.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @GetMapping("/profile")
    public ApiResponse<User> profile(Authentication authentication) {
        String username = authentication.getName();
        User user = authService.getUserByUsername(username);
        user.setPasswordHash(null);
        return ApiResponse.success(user);
    }
}
