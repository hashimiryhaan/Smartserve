package com.smartserve.api.claims;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartserve.api.food.FoodListing;
import com.smartserve.api.users.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 4)
    private String otp;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "claimedat", updatable = false)
    private LocalDateTime claimedAt = LocalDateTime.now(); 

    @Column(name = "verifiedat")
    private LocalDateTime verifiedAt;

    /**
     * FIX: Removed @JsonIgnore. 
     * Using @JsonIgnoreProperties to prevent infinite loops by ignoring the 
     * 'claims' list inside the FoodListing object during serialization.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "foodid")
    @JsonIgnoreProperties("claims") 
    private FoodListing food;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claimantid")
    @JsonIgnoreProperties({"claims", "password"}) // Also hide user passwords for safety
    private User claimant;

    public enum ClaimStatus {
        PENDING, VERIFIED, CANCELLED
    }
}