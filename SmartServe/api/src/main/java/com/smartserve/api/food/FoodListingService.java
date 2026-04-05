package com.smartserve.api.food;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FoodListingService {

    @Autowired
    private FoodListingRepository foodRepository;

    // 1. ADDED: Filter by Category ID (Matches the wireframe dropdown)
    public List<FoodListing> getFoodByCategoryId(Integer categoryId) {
        return foodRepository.findByCategoryId(categoryId);
    }

    // 2. ADDED: Stats for [X] Meals Saved
    // In your SQL status enum, once an item is 'CLAIMED', it counts as a meal saved.
    public long countCompletedMeals() {
        return foodRepository.countByStatus(FoodListing.ListingStatus.CLAIMED);
    }

    // 3. ADDED: Stats for [Z] Inventory Cleared
    // This counts the total number of items ever donated in smartserve.
    public long countTotalDonations() {
        return foodRepository.count();
    }

    // --- YOUR EXISTING METHODS BELOW ---

    public FoodListing saveFood(FoodListing foodListing) {
        if (foodListing.getId() == null) {
            foodListing.setStatus(FoodListing.ListingStatus.AVAILABLE);
        }
        return foodRepository.save(foodListing);
    }

    public List<FoodListing> getAvailableFood() {
        return foodRepository.findByStatus(FoodListing.ListingStatus.AVAILABLE);
    }

    public List<FoodListing> getListingsByDonor(Integer donorId) {
        return foodRepository.findByDonorId(donorId);
    }

    public void deleteListing(Integer id) {
        foodRepository.deleteById(id);
    }

    public Optional<FoodListing> getFoodById(Integer id) {
        return foodRepository.findById(id);
    }
    
    // Inside FoodListingService.java
public List<FoodListing> getAvailableDonations() {
    return foodRepository.findByStatusAndListingType(
        FoodListing.ListingStatus.AVAILABLE, 
        FoodListing.ListingType.DONATION
    );
}
}