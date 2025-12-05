package com.skillswap.dto;

public class AuthRequest {
    private String username;
    private String password;

    public AuthRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}
