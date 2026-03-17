package com.storeshop.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user in the store.
 */
@Entity
@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class User {
    @Id
    private String userId;
    
    @Column(unique = true)
    private String username;

    private String password;
    private String email;
    private Role role;
}
