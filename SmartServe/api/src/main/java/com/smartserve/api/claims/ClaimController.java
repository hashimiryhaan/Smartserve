package com.smartserve.api.claims;

import com.smartserve.api.food.FoodListing;
import com.smartserve.api.food.FoodListingRepository;
import com.smartserve.api.users.User;
import com.smartserve.api.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "*") 
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private FoodListingRepository foodListingRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 1. Place a Claim
     * Triggered by the "CLAIM THIS FOOD" button.
     */
    @PostMapping("/add")
    public ResponseEntity<Claim> placeClaim(@RequestParam Integer foodId, @RequestParam Integer claimantId) {
        // 1. Fetch entities from DB
        FoodListing food = foodListingRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food listing not found"));
        
        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Prepare the claim request
        Claim claimRequest = new Claim();
        
        // Use setFood() because your variable name is 'food'
        claimRequest.setFood(food); 
        claimRequest.setClaimant(claimant);

        // 3. Hand over to service for OTP generation and status management
        Claim savedClaim = claimService.createClaim(claimRequest);
        return ResponseEntity.ok(savedClaim);
    }

    /**
     * 2. Verify OTP
     * Used by the Donor Dashboard.
     */
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Integer id, @RequestParam String otp) {
        try {
            Claim verifiedClaim = claimService.verifyClaim(id, otp);
            return ResponseEntity.ok(verifiedClaim);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 3. Get Pending Pickups
     */
    @GetMapping("/pending")
    public List<Claim> getPendingClaims() {
        return claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == Claim.ClaimStatus.PENDING)
                .toList();
    }
    /**
     * 4. Get Order History for a specific Customer
     * This shows all claims (Pending, Claimed, Expired) and their OTPs.
     */
    @GetMapping("/history/customer/{claimantId}")
    public ResponseEntity<List<Claim>> getCustomerOrderHistory(@PathVariable Integer claimantId) {
        // You'll need to add findByClaimantId in your ClaimRepository
        List<Claim> history = claimRepository.findByClaimant_Id(claimantId);
        return ResponseEntity.ok(history);
    }

    /**
     * 5. Get Food Listing History for a specific Donor
     * This shows all food items a donor has ever posted.
     */
    @GetMapping("/history/donor/{donorId}")
    public ResponseEntity<List<FoodListing>> getDonorListingHistory(@PathVariable Integer donorId) {
        // You'll need to add findByDonorId in your FoodListingRepository
        List<FoodListing> history = foodListingRepository.findByDonor_Id(donorId);
        return ResponseEntity.ok(history);
    }
}