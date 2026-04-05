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

    // The OTP column (Kept the one with constraints)
    @Column(nullable = false, length = 4)
    private String otp;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status = ClaimStatus.PENDING;

    // Tracks if this was a standard "USER" claim or a "CHARITY" rescue
    @Column(name = "claimertype")
    private String claimerType; 

    // Denormalized name for faster rendering in the Donor Dashboard
    @Column(name = "claimername")
    private String claimerName;

    @Column(name = "claimedat", updatable = false)
    private LocalDateTime claimedAt = LocalDateTime.now(); 

    @Column(name = "verifiedat")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "foodid")
    @JsonIgnoreProperties("claims") 
    private FoodListing food;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claimantid")
    // Added "foodListings" here just in case your User entity maps back to food!
    @JsonIgnoreProperties({"claims", "password", "foodListings"}) 
    private User claimant;

    public enum ClaimStatus {
        PENDING, VERIFIED, CANCELLED
    }
}