package com.smartserve.api.claims;

import com.smartserve.api.food.FoodListing;
import com.smartserve.api.food.FoodListingRepository;
import com.smartserve.api.users.User;
import com.smartserve.api.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
     * Updated to handle the Charity Connection detection.
     */
    @PostMapping("/add")
    public ResponseEntity<Claim> placeClaim(@RequestParam Integer foodId, @RequestParam Integer claimantId) {
        FoodListing food = foodListingRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food listing not found"));
        
        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Claim claimRequest = new Claim();
        claimRequest.setFood(food); 
        claimRequest.setClaimant(claimant);

        // NEW: Integrate Charity Connection Logic
        // We set the type based on the User's role (ensure your User entity has a role field)
        // Replace the old line 50 with this:
        claimRequest.setClaimerType(claimant.getRole() != null ? claimant.getRole().name() : "USER");
        claimRequest.setClaimerName(claimant.getName());

        Claim savedClaim = claimService.createClaim(claimRequest);
        return ResponseEntity.ok(savedClaim);
    }

    /**
     * 2. Verify OTP
     * Used by the Donor Dashboard to complete a rescue.
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
     * 3. Get User Claim History (FOR HISTORY PAGE)
     * Fetches all rescues made by a specific user using the repository query.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Claim>> getUserClaims(@PathVariable Integer userId) {
        // Optimized: uses the repository method we refined earlier
        List<Claim> userHistory = claimRepository.findByClaimantIdOrderByIdDesc(userId);
        return ResponseEntity.ok(userHistory);
    }

    /**
     * 4. Get Pending Pickups
     * Fetches only active claims that haven't been picked up yet.
     */
    @GetMapping("/pending")
    public List<Claim> getPendingClaims() {
        return claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == Claim.ClaimStatus.PENDING)
                .toList();
    }

    /**
     * 5. Cancel a Claim
     * Changes status to CANCELLED and returns food quantity to the listing.
     */
    @PostMapping("/cancel/{id}")
    @Transactional
    public ResponseEntity<?> cancelClaim(@PathVariable Integer id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        // Only PENDING claims can be cancelled
        if (claim.getStatus() == Claim.ClaimStatus.PENDING) {
            // 1. Update Status
            claim.setStatus(Claim.ClaimStatus.CANCELLED);
            claimRepository.save(claim);

            // 2. Increment Food Quantity back
            FoodListing food = claim.getFood();
            if (food != null) {
                food.setQuantity(food.getQuantity() + 1);
                foodListingRepository.save(food);
            }

            return ResponseEntity.ok(Map.of("message", "Claim cancelled and food restored."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot cancel a claim that is already verified or cancelled."));
        }
    }
    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        try {
            return ResponseEntity.ok(claimService.getAllClaims());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}