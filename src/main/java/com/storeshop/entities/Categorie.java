package com.storeshop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product category used for grouping and filtering products in the storefront.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categorie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Products linked to this category through {@code Produit.categorie}. */
  @OneToMany(mappedBy = "categorie")
  private List<Produit> produits;

  /** Category display name (must be unique for new inserts). */
  private String nom;
}
