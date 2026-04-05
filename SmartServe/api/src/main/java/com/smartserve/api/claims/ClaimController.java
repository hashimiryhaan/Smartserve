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
@CrossOrigin(origins = "*") // Allows your frontend on port 5500 to talk to port 8080
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
     * 1. Place a Claim (UPDATED FOR JSON PAYLOAD)
     * We now use @RequestBody so Spring Boot reads the JSON payload sent from item-details.html
     */
    @PostMapping("/add")
    public ResponseEntity<?> placeClaim(@RequestBody Claim claimRequest) {
        try {
            // We need to fetch the User to grab their Role and Name for your Charity tracking
            if (claimRequest.getClaimant() != null && claimRequest.getClaimant().getId() != null) {
                User claimant = userRepository.findById(claimRequest.getClaimant().getId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                
                claimRequest.setClaimerType(claimant.getRole() != null ? claimant.getRole().name() : "USER");
                claimRequest.setClaimerName(claimant.getName());
            }

            // claimRequest now automatically contains the 'claimedQuantity' from the frontend!
            Claim savedClaim = claimService.createClaim(claimRequest);
            
            return ResponseEntity.ok(savedClaim);
            
        } catch (Exception e) {
            // Return a clean error message to the frontend if something fails (like not enough food)
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 2. Verify OTP
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
     * 3. Get User Claim History
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Claim>> getUserClaims(@PathVariable Integer userId) {
        List<Claim> userHistory = claimRepository.findByClaimantIdOrderByIdDesc(userId);
        return ResponseEntity.ok(userHistory);
    }

    /**
     * 4. Get Pending Pickups
     */
    @GetMapping("/pending")
    public List<Claim> getPendingClaims() {
        return claimRepository.findAll().stream()
                .filter(c -> c.getStatus() == Claim.ClaimStatus.PENDING)
                .toList();
    }

    /**
     * 5. Cancel a Claim
     * Fixed to return the exact quantity requested, rather than just 1.
     */
    @PostMapping("/cancel/{id}")
    @Transactional
    public ResponseEntity<?> cancelClaim(@PathVariable Integer id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        if (claim.getStatus() == Claim.ClaimStatus.PENDING) {
            claim.setStatus(Claim.ClaimStatus.CANCELLED);
            claimRepository.save(claim);

            FoodListing food = claim.getFood();
            if (food != null) {
                // Restore the exact amount that was booked
                int qtyToRestore = claim.getClaimedQuantity() != null ? claim.getClaimedQuantity() : 1;
                food.setQuantity(food.getQuantity() + qtyToRestore);
                
                // If the item was hidden because it hit 0, make it available again
                if (food.getStatus() == FoodListing.ListingStatus.CLAIMED) {
                    food.setStatus(FoodListing.ListingStatus.AVAILABLE);
                }
                
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