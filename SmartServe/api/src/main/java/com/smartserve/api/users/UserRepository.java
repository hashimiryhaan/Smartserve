package com.smartserve.api.users;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Spring Data JPA automatically writes the SQL for this!
    Optional<User> findByEmail(String email);
}
