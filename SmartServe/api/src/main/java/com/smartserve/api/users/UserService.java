package com.smartserve.api.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. REGISTER: Used for the Sign-Up wireframe
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered!");
        }
        return userRepository.save(user);
    }

    // 2. LOGIN: Used for the Login wireframe
    public User loginUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }

    // 3. GET ALL: For internal dashboard or testing
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 4. FIND BY ID: Useful for linking food donors or claimants
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // 5. ADDED: This fixes the error in your Controller!
    // It provides the [Y] Users Joined count for the landing page
    public long countTotalUsers() {
        return userRepository.count(); 
    }
}