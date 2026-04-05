package com.smartserve.api.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY LAYER: Handles all database interactions for the 'users' table.
 * Spring Data JPA automatically implements these methods based on their names.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 1. LOGIN: Finds a user by their unique email address
    Optional<User> findByEmail(String email);

    // 2. SIGNUP: Checks if an email already exists in the database
    boolean existsByEmail(String email);

    // 3. ADMIN: Fetches users filtered by their Role (DONOR/CLAIMANT) and Approval status
    // This is used for fetching "Pending Donors" (Role.DONOR, false)
    List<User> findByRoleAndApproved(User.Role role, boolean approved);

    // 4. STATS: Counts users of a specific role for the dashboard
    long countByRole(User.Role role);

    // 5. STATS: Counts pending approvals (useful for the Admin badge or dashboard stats)
    long countByRoleAndApproved(User.Role role, boolean approved);
}