package com.skillswap.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private String email;
    private String displayName;
    private String contact;
    private String bio;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String d) { this.displayName = d; }

    public String getContact() { return contact; }
    public void setContact(String c) { this.contact = c; }

    public String getBio() { return bio; }
    public void setBio(String b) { this.bio = b; }
}
