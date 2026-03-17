package com.storeshop.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a product category in the store.
 */
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Categorie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @OneToMany(mappedBy = "categorie")
  private List<Produit> produits;

  private String nom;
}
