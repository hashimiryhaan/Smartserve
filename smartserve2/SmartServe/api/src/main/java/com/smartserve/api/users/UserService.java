package com.smartserve.api.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Separate directories for different document types
    private final String LICENSE_DIR = "uploads/licenses/";
    private final String PROFILE_DIR = "uploads/profiles/";

    /**
     * REGISTER WITH FILE: Handles the physical file saving for business licenses.
     */
    public User registerUserWithFile(User user, MultipartFile file) throws IOException {
        // 1. Check if email exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("This email is already registered!");
        }

        // 2. Handle Donor License Upload
        if (user.getRole() == User.Role.DONOR && file != null && !file.isEmpty()) {
            String fileName = saveFile(file, LICENSE_DIR);
            user.setLicensePath(fileName);
            user.setApproved(false); // Donors must wait for Admin
        } else {
            // Students/Claimants/Admins are auto-approved
            user.setApproved(true);
        }

        return userRepository.save(user);
    }

    /**
     * UPDATE PROFILE WITH FILE: Updates text fields and handles profile picture upload.
     */
    public User updateProfileWithFile(Integer id, Map<String, String> profileData, MultipartFile file) throws IOException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update text fields if they exist in the request
        if (profileData.get("name") != null) {
            existingUser.setName(profileData.get("name"));
        }
        if (profileData.get("address") != null) {
            existingUser.setAddress(profileData.get("address"));
        }
        if (profileData.get("phone") != null) {
            existingUser.setPhone(profileData.get("phone"));
        }

        // Handle Profile Picture Upload
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file, PROFILE_DIR);
            existingUser.setProfilePicPath(fileName);
        }

        return userRepository.save(existingUser);
    }

    /**
     * HELPER METHOD: Physical file saving logic to reduce code duplication.
     */
    private String saveFile(MultipartFile file, String uploadDir) throws IOException {
        // Create unique filename
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        // Ensure directory exists
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

    /**
     * LOGIN: Validates email and password.
     */
    public User loginUser(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Invalid email or password. Please try again."));
    }

    /**
     * ADMIN: Retrieves users based on role and approval status.
     */
    public List<User> getUsersByRoleAndApproval(User.Role role, boolean approved) {
        return userRepository.findByRoleAndApproved(role, approved);
    }

    /**
     * ADMIN: Sets a user's approved status to true.
     */
    public User approveUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setApproved(true);
        return userRepository.save(user);
    }

    /**
     * FETCH ALL: Returns all registered users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * FETCH BY ID: Returns a specific user by their database ID.
     */
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    /**
     * STATS: Returns the count of all users.
     */
    public long countTotalUsers() {
        return userRepository.count(); 
    }
}