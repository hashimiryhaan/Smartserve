package com.smartserve.api.claims;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // FIX: Added @JsonIgnore here to break the infinite loop with FoodListing
    @ManyToOne
    @JoinColumn(name = "foodid")
    @JsonIgnore 
    private FoodListing food;

    @ManyToOne
    @JoinColumn(name = "claimantid")
    @JsonIgnore
    private User claimant;

    public enum ClaimStatus {
        PENDING, VERIFIED, CANCELLED
    }
}