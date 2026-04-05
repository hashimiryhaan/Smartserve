package com.smartserve.api.food;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.smartserve.api.category.Category;
import com.smartserve.api.claims.Claim;
import com.smartserve.api.users.User;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "foodlistings") 
@Data
public class FoodListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    private String description; 

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "categoryid") 
    private Category category; 

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "addresstext")
    private String addressText;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "discounted_price")
    private BigDecimal discountedPrice;

    @Column(name = "pickup_time")
    private String pickupTime;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "expirytime") 
    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    private ListingStatus status = ListingStatus.AVAILABLE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "donorid")
    @JsonIgnoreProperties({"password", "claims", "foodListings"}) // THE FIX 
    private User donor;

    // FIX: Added @JsonIgnore to prevent the infinite JSON loop with Claims
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    @JsonIgnore 
    private List<Claim> claims;

    public enum ListingStatus {
        AVAILABLE, CLAIMED, EXPIRED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type")
    private ListingType listingType = ListingType.SALE;

    public enum ListingType {
        SALE, DONATION
    }
}