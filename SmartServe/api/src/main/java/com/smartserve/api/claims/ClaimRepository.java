package com.smartserve.api.claims;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Integer> {
    
    // Find claims made by a specific user (for their "My Claims" history)
    List<Claim> findByClaimantId(Integer claimantId);
    
    // Find claims for a specific food item
    List<Claim> findByFoodId(Integer foodId);

    List<Claim> findByClaimant_Id(Integer claimantId);
}
