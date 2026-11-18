package com.smartserve.api.claims;

import com.smartserve.api.food.FoodListing;
import com.smartserve.api.food.FoodListingRepository; // Corrected naming
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
    private FoodListingRepository foodListingRepository; // Match your repository name

    @Transactional
    public Claim createClaim(Claim claim) {
        // 1. Generate a random 4-digit OTP
        String randomOtp = String.valueOf(1000 + new Random().nextInt(9000));
        claim.setOtp(randomOtp);
        claim.setStatus(Claim.ClaimStatus.PENDING);
        claim.setClaimedAt(LocalDateTime.now()); // Ensure naming matches entity (camelCase)
        
        // 2. Mark the food as CLAIMED so others can't see it
        FoodListing food = foodListingRepository.findById(claim.getFood().getId())
                .orElseThrow(() -> new RuntimeException("Food listing not found"));
        
        food.setStatus(FoodListing.ListingStatus.CLAIMED); // Match the Enum name in FoodListing
        foodListingRepository.save(food);

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim verifyClaim(Integer claimId, String inputOtp) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (claim.getOtp().equals(inputOtp)) {
            claim.setStatus(Claim.ClaimStatus.VERIFIED);
            claim.setVerifiedAt(LocalDateTime.now()); // Corrected to camelCase
            return claimRepository.save(claim);
        } else {
            throw new RuntimeException("Invalid OTP. Verification failed.");
        }
    }
}