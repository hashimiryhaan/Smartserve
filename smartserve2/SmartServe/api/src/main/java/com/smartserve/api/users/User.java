package com.smartserve.api.users;

import jakarta.persistence.*;
import lombok.Data;

/**
 * USER ENTITY: Maps directly to your 'users' table in MySQL.
 * Updated to include profile picture support.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;
    
    private String address; 

    @Column(name = "user_type")
    private String userType; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; 

    @Enumerated(EnumType.STRING)
    @Column(name = "businesstype")
    private BusinessType businessType = BusinessType.INDIVIDUAL;

    // Matches the 'approved' column (0 or 1 in MySQL)
    @Column(nullable = false)
    private boolean approved = false; 

    // Field for Business License path
    @Column(name = "license_path")
    private String licensePath;

    // NEW COLUMN: Path to the profile picture file
    @Column(name = "profile_pic_path")
    private String profilePicPath;

    /**
     * Role Definitions
     */
    public enum Role { 
        DONOR, 
        CLAIMANT,
        ADMIN 
    }
    
    /**
     * Business Type Definitions
     */
    public enum BusinessType { 
        RESTAURANT, 
        WEDDINGHOST, 
        INDIVIDUAL,
        MANAGEMENT 
    }
}