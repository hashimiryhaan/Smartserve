package com.smartserve.api.claims;

import com.smartserve.api.food.FoodListing;
import com.smartserve.api.food.FoodListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private FoodListingRepository foodListingRepository;

    /**
     * Creates a new claim, generates an OTP, and hides the food from the marketplace.
     */
    @Transactional
    public Claim createClaim(Claim claim) {
        // 1. Generate a random 4-digit OTP
        String randomOtp = String.valueOf(1000 + new Random().nextInt(9000));
        claim.setOtp(randomOtp);
        claim.setStatus(Claim.ClaimStatus.PENDING);
        claim.setClaimedAt(LocalDateTime.now());
        
        // 2. Mark the food as CLAIMED so others can't see it in the marketplace
        FoodListing food = foodListingRepository.findById(claim.getFood().getId())
                .orElseThrow(() -> new RuntimeException("Food listing not found"));
        
        // Ensure naming matches your FoodListing.ListingStatus enum (e.g., CLAIMED)
        food.setStatus(FoodListing.ListingStatus.CLAIMED); 
        foodListingRepository.save(food);

        return claimRepository.save(claim);
    }

    /**
     * Verifies the OTP provided by the customer at the time of pickup.
     */
    @Transactional
    public Claim verifyClaim(Integer claimId, String inputOtp) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (!claim.getStatus().equals(Claim.ClaimStatus.PENDING)) {
            throw new RuntimeException("This claim is already " + claim.getStatus());
        }

        if (claim.getOtp().equals(inputOtp)) {
            claim.setStatus(Claim.ClaimStatus.VERIFIED);
            claim.setVerifiedAt(LocalDateTime.now());
            return claimRepository.save(claim);
        } else {
            throw new RuntimeException("Invalid OTP. Verification failed.");
        }
    }

    /**
     * Cancels a pending claim and makes the food available in the marketplace again.
     */
    @Transactional
    public void cancelClaim(Integer claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (!claim.getStatus().equals(Claim.ClaimStatus.PENDING)) {
            throw new RuntimeException("Only pending claims can be cancelled.");
        }

        // 1. Update Claim status
        claim.setStatus(Claim.ClaimStatus.CANCELLED);
        claimRepository.save(claim);

        // 2. Make the FoodListing available again
        FoodListing food = foodListingRepository.findById(claim.getFood().getId())
                .orElseThrow(() -> new RuntimeException("Associated food listing not found"));
        
        // Use the status name that makes it show back up on the home page (usually AVAILABLE)
        food.setStatus(FoodListing.ListingStatus.AVAILABLE); 
        foodListingRepository.save(food);
    }
}