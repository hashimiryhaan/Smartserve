package com.smartserve.api.users;

import jakarta.persistence.*;
import lombok.Data;

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

    private String password;

    private String phone;

    // Fields to match your Profile Wireframe
    private String address; 
    private String userType = "FOOD FINDER"; 

    @Enumerated(EnumType.STRING)
    private Role role; 

    @Enumerated(EnumType.STRING)
    @Column(name = "businesstype")
    private BusinessType businessType = BusinessType.INDIVIDUAL;

    public enum Role { DONOR, CLAIMANT }
    public enum BusinessType { RESTAURANT, WEDDINGHOST, INDIVIDUAL }
}
