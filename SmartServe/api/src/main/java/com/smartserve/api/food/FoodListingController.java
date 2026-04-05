package com.smartserve.api.food;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/food")
@CrossOrigin(origins = "*") 
public class FoodListingController {

    @Autowired
    private FoodListingService foodService;

    private final String UPLOAD_DIR = "uploads/";

    @Autowired
    private FoodListingService foodListingService;

    // 1. GLOBAL VIEW: All items for general fetch
    @GetMapping
    public List<FoodListing> getAllFood() {
        return foodService.getAvailableFood();
    }

    // 2. ADD: Logic for New Listings
    @PostMapping(value = "/add", consumes = {"multipart/form-data"})
    public ResponseEntity<FoodListing> addFood(
            @RequestPart(value = "file", required = false) MultipartFile file, // FIXED: Made file optional
            @RequestPart("data") FoodListing foodListing) {
        
        try {
            // FIXED: Only try to save the image if a file was actually uploaded
            if (file != null && !file.isEmpty()) {
                String fileName = saveImageFile(file);
                foodListing.setImageUrl("/uploads/" + fileName);
            } else {
                // Set a default image URL if no photo is uploaded
                foodListing.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500");
            }
            
            foodListing.setCreatedAt(LocalDateTime.now()); 
            
            if (foodListing.getStatus() == null) {
                foodListing.setStatus(FoodListing.ListingStatus.AVAILABLE);
            }
            
            return ResponseEntity.ok(foodListingService.saveFood(foodListing));
            
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build(); // Graceful error handling
        }
    }

    // 3. UPDATE: Now handles Multipart so you can update images too!
    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<FoodListing> updateFood(
            @RequestPart(value = "file", required = false) MultipartFile file, 
            @RequestPart("data") FoodListing details) throws IOException {
        
        return foodService.getFoodById(details.getId()).map(existingFood -> {
            // Update Text Fields
            existingFood.setTitle(details.getTitle());
            existingFood.setQuantity(details.getQuantity());
            existingFood.setAddressText(details.getAddressText());
            existingFood.setOriginalPrice(details.getOriginalPrice());
            existingFood.setDiscountedPrice(details.getDiscountedPrice());
            existingFood.setPickupTime(details.getPickupTime());
            existingFood.setCategory(details.getCategory());
            existingFood.setUpdatedAt(LocalDateTime.now());

            // Update Image only if a new file is provided
            if (file != null && !file.isEmpty()) {
                try {
                    String fileName = saveImageFile(file);
                    existingFood.setImageUrl("/uploads/" + fileName);
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed");
                }
            }
            
            return ResponseEntity.ok(foodService.saveFood(existingFood));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. DELETE: Removes from database
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFood(@PathVariable Integer id) {
        foodService.deleteListing(id);
        return ResponseEntity.ok("Listing deleted successfully");
    }

    // --- Helper Methods ---

    private String saveImageFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return fileName;
    }

    // --- Other Endpoints ---

    @GetMapping("/{id}")
    public ResponseEntity<FoodListing> getFoodById(@PathVariable Integer id) {
        return foodService.getFoodById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/donor/{donorId}")
    public List<FoodListing> getByDonor(@PathVariable Integer donorId) {
        return foodService.getListingsByDonor(donorId);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getLandingPageStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("mealsSaved", foodService.countCompletedMeals()); 
        stats.put("inventoryCleared", foodService.countTotalDonations());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/available")
    public List<FoodListing> getAllAvailable() {
        return foodService.getAvailableFood();
    }
    @GetMapping("/donations")
    public List<FoodListing> getDonationsForCharity() {
        return foodListingService.getAvailableDonations();
    }
}