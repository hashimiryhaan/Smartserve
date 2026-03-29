package com.smartserve.api.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodListingRepository extends JpaRepository<FoodListing, Integer> {
    
    // 1. Allows filtering by the Category ID (Vegetarian, Bakery, etc.)
    List<FoodListing> findByCategoryId(Integer categoryId);

    // 2. Donor specific view (e.g., Lulu Restaurant's own dashboard)
    List<FoodListing> findByDonorId(Integer donorId);
    
    // 3. Status filter (Used by Arjun to see what is currently Available)
    List<FoodListing> findByStatus(FoodListing.ListingStatus status);

    // 4. ADDED: This fixes the 'countByStatus' error in your Service
    // This will provide the [X] Meals Saved stat for your Landing Page
    long countByStatus(FoodListing.ListingStatus status);
}