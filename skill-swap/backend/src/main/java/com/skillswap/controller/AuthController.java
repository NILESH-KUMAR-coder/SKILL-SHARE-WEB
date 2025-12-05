package com.skillswap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillswap.dto.AuthRequest;
import com.skillswap.dto.AuthResponse;
import com.skillswap.dto.RegisterRequest;
import com.skillswap.model.User;
import com.skillswap.service.UserService;
import com.skillswap.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.getUsername() == null || req.getPassword() == null || req.getName() == null || req.getEmail() == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "username, password, name and email are required"));
        }

        try {
            User user = userService.register(
                    req.getUsername(),
                    req.getPassword(),
                    req.getName(),
                    req.getEmail(),
                    req.getDisplayName(),
                    req.getContact(),
                    req.getBio()
            );
            String token = jwtUtil.generateToken(user.getUsername());
            AuthResponse resp = new AuthResponse(token, user.getUsername(), user.getDisplayName());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        if (req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "username and password required"));
        }
        User user = userService.login(req.getUsername(), req.getPassword());
        if (user == null) {
            return ResponseEntity.status(401).body(java.util.Map.of("error", "invalid credentials"));
        }
        String token = jwtUtil.generateToken(user.getUsername());
        AuthResponse resp = new AuthResponse(token, user.getUsername(), user.getDisplayName());
        return ResponseEntity.ok(resp);
    }
}
