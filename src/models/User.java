package models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private String userId;
    private String username;
    private String passwordHash;
    private String email;
    private String role; // "host", "guest", "admin"
    private LocalDateTime createdAt;
    private boolean isActive;

    public User(String userId, String username, String passwordHash, String email, String role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive() { return isActive; }
    
    public void setActive(boolean active) { isActive = active; }
    
    @Override
    public String toString() {
        return String.join(",",
            userId, username, passwordHash, email, role,
            createdAt.toString(), String.valueOf(isActive)
        );
    }

    public static User fromString(String data) {
        String[] parts = data.split(",");
        User user = new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
        user.createdAt = LocalDateTime.parse(parts[5]);
        user.isActive = Boolean.parseBoolean(parts[6]);
        return user;
    }
}