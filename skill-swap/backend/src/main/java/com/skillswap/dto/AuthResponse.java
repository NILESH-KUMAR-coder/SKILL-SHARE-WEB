package com.skillswap.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String displayName;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String displayName) {
        this.token = token;
        this.username = username;
        this.displayName = displayName;
    }

    public String getToken() { return token; }
    public void setToken(String t) { this.token = t; }

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String d) { this.displayName = d; }
}
