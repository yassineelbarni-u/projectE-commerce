package com.storeshop.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Application user persisted in database.
 *
 * <p>Used for both authentication (username/password/role) and profile identity.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
  /** Stable identifier generated as UUID string. */
  @Id private String userId;

  /** Unique login name used during authentication. */
  @Column(unique = true)
  private String username;

  /** Password hash (not plain password). */
  private String password;

  /** Contact email address. */
  private String email;

  /** Authorization role controlling access to protected routes. */
  private Role role;

  /** Orders placed by this user. */
  @OneToMany(mappedBy = "user", cascade = jakarta.persistence.CascadeType.REMOVE, orphanRemoval = true)
  private List<Commande> commandes;
}
