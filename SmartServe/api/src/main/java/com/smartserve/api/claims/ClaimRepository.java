package com.smartserve.api.claims;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Integer> {
    
    /**
     * Finds all claims made by a specific user (claimant).
     * OrderByIdDesc ensures the most recent "Rescues" appear at the top of the history page.
     * * Note: Spring Data JPA automatically maps 'claimantId' to the 'id' field 
     * of the 'User claimant' object in your Claim entity.
     */
    List<Claim> findByClaimantIdOrderByIdDesc(Integer claimantId);
    
    /**
     * Find claims for a specific food item.
     * Useful for donors to see how many people have claimed a specific listing.
     */
    List<Claim> findByFoodId(Integer foodId);

    /**
     * Finds claims by status (e.g., PENDING, VERIFIED, CANCELLED).
     * Useful for filtering the history or admin dashboard.
     */
    List<Claim> findByStatus(Claim.ClaimStatus status);
}