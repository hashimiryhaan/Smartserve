package com.smartserve.api.users;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") 
public class UserController {

    @Autowired
    private UserService userService;

    // 1. Get all users
    @GetMapping("/all")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * 2. SIGNUP
     * Handles 'multipart/form-data' for user data and business license.
     */
    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> signup(
            @RequestPart("user") String userJson, 
            @RequestPart(value = "license", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            User newUser = mapper.readValue(userJson, User.class);

            if (newUser.getBusinessType() == null) {
                newUser.setBusinessType(User.BusinessType.INDIVIDUAL);
            }

            User savedUser = userService.registerUserWithFile(newUser, file);
            return ResponseEntity.ok(savedUser);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid Role or Business Type provided.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Signup Error: " + e.getMessage());
        }
    }

    // 3. LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> data) {
        try {
            User user = userService.loginUser(data.get("email"), data.get("password"));
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // 4. ADMIN: Get pending donors
    @GetMapping("/pending-donors")
    public ResponseEntity<List<User>> getPendingDonors() {
        return ResponseEntity.ok(userService.getUsersByRoleAndApproval(User.Role.DONOR, false));
    }

    // 5. ADMIN: Approve donor
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveDonor(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(userService.approveUser(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    /**
     * 6. UPDATE PROFILE (FIXED)
     * Handles both JSON (Standard updates) and Multipart (Profile Pic updates)
     */
    @PutMapping(value = "/update-profile/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Integer id, 
            @RequestBody Map<String, String> data) { 
        try {
            // This handles the simple JSON update from your current frontend
            User updatedUser = userService.updateProfileWithFile(id, data, null);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update Error: " + e.getMessage());
        }
    }

    // Overloaded method to handle Multipart if you add a Profile Picture later
    @PutMapping(value = "/update-profile/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateProfileWithFile(
            @PathVariable Integer id, 
            @RequestPart("userData") String userDataJson,
            @RequestPart(value = "profilePic", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(userDataJson, new TypeReference<Map<String, String>>() {});
            
            User updatedUser = userService.updateProfileWithFile(id, data, file);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update Error: " + e.getMessage());
        }
    }

    // 7. DASHBOARD STATS
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getAdminStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userService.countTotalUsers());
        return ResponseEntity.ok(stats);
    }
}