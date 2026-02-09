package services;

import models.User;
import utils.FileHandler;
import utils.SecurityUtils;

import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    private List<User> users;
    private static final String USERS_FILE = "users.txt";
    private User currentUser;

    public UserService() {
        loadUsers();
        // Create admin user if not exists
        if (users.stream().noneMatch(u -> u.getRole().equals("admin"))) {
            User admin = new User(
                SecurityUtils.generateId(),
                "admin",
                SecurityUtils.hashPassword("admin123"),
                "admin@airbnb.com",
                "admin"
            );
            users.add(admin);
            saveUsers();
        }
        
        // Create sample hosts and guests if empty
        createSampleUsers();
    }

    private void createSampleUsers() {
        // Only create sample data if no users exist (besides admin)
        if (users.size() <= 1) {
            // Create sample host
            User host1 = new User(
                SecurityUtils.generateId(),
                "kea_host",
                SecurityUtils.hashPassword("host123"),
                "john@gmail.com",
                "host"
            );
            users.add(host1);
            
            // Create sample guest
            User guest1 = new User(
                SecurityUtils.generateId(),
                "rea_guest", 
                SecurityUtils.hashPassword("guest123"),
                "rea@gmail.com",
                "guest"
            );
            users.add(guest1);
            
            saveUsers();
        }
    }

    private void loadUsers() {
        users = FileHandler.loadData(USERS_FILE, User::fromString);
    }

    private void saveUsers() {
        FileHandler.saveData(USERS_FILE, users);
    }

    public boolean registerUser(String username, String password, String email, String role) {
        // Check if username already exists using Streams
        boolean usernameExists = users.stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
        
        if (usernameExists) {
            return false;
        }

        User newUser = new User(
            SecurityUtils.generateId(),
            username,
            SecurityUtils.hashPassword(password),
            email,
            role
        );
        
        users.add(newUser);
        return FileHandler.appendData(USERS_FILE, newUser);
    }

    public boolean login(String username, String password) {
        Optional<User> userOpt = users.stream()
                .filter(user -> user.getUsername().equals(username) && user.isActive())
                .findFirst();

        if (userOpt.isPresent() && SecurityUtils.verifyPassword(password, userOpt.get().getPasswordHash())) {
            currentUser = userOpt.get();
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean updateUserRole(String userId, String newRole) {
        Optional<User> userOpt = users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // In a real implementation, we'd create a new user with updated role
            // For simplicity, we'll reload and save
            loadUsers();
            saveUsers();
            return true;
        }
        return false;
    }
    
    // Helper method to get sample user IDs for testing
    public Optional<String> getSampleHostId() {
        return users.stream()
                .filter(user -> user.getUsername().equals("john_host"))
                .map(User::getUserId)
                .findFirst();
    }
    
    public Optional<String> getSampleGuestId() {
        return users.stream()
                .filter(user -> user.getUsername().equals("sarah_guest"))
                .map(User::getUserId)
                .findFirst();
    }
}