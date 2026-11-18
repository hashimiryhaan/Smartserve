package com.smartserve.api.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")

public class UserController {

    @Autowired
    private UserService userService;

    // 1. GET ALL USERS
    @GetMapping("/all")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    // 2. SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> signupData) {
        try {
            User newUser = new User();
            newUser.setEmail(signupData.get("email"));
            newUser.setPassword(signupData.get("password"));
            newUser.setPhone(signupData.get("phone")); // Added to capture ph no from your screenshot
            
            // Default setup for new signups like 'Ryhan'
            newUser.setName("New User"); 
            newUser.setRole(User.Role.CLAIMANT); 
            newUser.setBusinessType(User.BusinessType.INDIVIDUAL);

            User registeredUser = userService.registerUser(newUser);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        try {
            User user = userService.loginUser(email, password);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 4. UPDATE PROFILE (Matches your yellow Profile Wireframe)
    @PutMapping("/update-profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer id, @RequestBody Map<String, String> profileData) {
        return userService.getUserById(id).map(existingUser -> {
            
            if (profileData.containsKey("name")) {
                existingUser.setName(profileData.get("name"));
            }
            if (profileData.containsKey("address")) {
                existingUser.setAddress(profileData.get("address"));
            }
            if (profileData.containsKey("phone")) {
                existingUser.setPhone(profileData.get("phone"));
            }
            
            // Handling the Toggle: Donor vs Food Finder
            if (profileData.containsKey("userType")) {
                String type = profileData.get("userType"); 
                if ("DONOR".equalsIgnoreCase(type)) {
                    existingUser.setRole(User.Role.DONOR);
                } else {
                    existingUser.setRole(User.Role.CLAIMANT);
                }
            }
            
            User updatedUser = userService.registerUser(existingUser);
            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. STATS for Homepage [Y]
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        Map<String, Long> stats = new HashMap<>();
        // This ensures the [Y] box on your homepage shows the real number from MySQL
        stats.put("totalUsers", userService.countTotalUsers());
        return ResponseEntity.ok(stats);
    }
}
