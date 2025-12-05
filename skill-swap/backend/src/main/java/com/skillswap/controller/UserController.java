package com.skillswap.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skillswap.model.User;
import com.skillswap.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        String username = auth.getName();
        User u = userService.findByUsername(username).orElse(null);
        if (u == null) return ResponseEntity.status(404).body(java.util.Map.of("error", "User not found"));
        return ResponseEntity.ok(u);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody java.util.Map<String, String> body, Authentication auth) {
        String username = auth.getName();
        String name = body.get("name");
        String displayName = body.get("displayName");
        String contact = body.get("contact");
        String bio = body.get("bio");
        String profileImage = body.get("profileImage");
        // Only update profileImage if a new one is provided
        if (profileImage != null && !profileImage.isEmpty()) {
            // Save the new profile image
            User saved = userService.updateProfile(username, name, displayName, contact, bio, profileImage);
            return ResponseEntity.ok(saved);
        }
        // If no new profile image, don't update that field
        User saved = userService.updateProfile(username, name, displayName, contact, bio, null);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/me/upload")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file, Authentication auth) {
        String username = auth.getName();
        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().body(java.util.Map.of("error", "No file"));

        // Validate file type (only image files)
        String contentType = file.getContentType();
        if (!contentType.startsWith("image")) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Only image files are allowed"));
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int i = filename.lastIndexOf('.');
        if (i >= 0) ext = filename.substring(i);
        String storedName = "profile_" + username + "_" + System.currentTimeMillis() + ext;

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(storedName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            userService.updateProfile(username, null, null, null, null, storedName);
            return ResponseEntity.ok(java.util.Map.of("filename", storedName, "url", storedName));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Could not save file"));
        }
    }
}
