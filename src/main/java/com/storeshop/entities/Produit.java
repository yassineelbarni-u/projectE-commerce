package com.storeshop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Product entity shown in catalog and managed by admins.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Category owning this product (many products can belong to one category). */
  @ManyToOne private Categorie categorie;

  /** Display name shown in lists and detail pages. */
  private String name;

  /** Public URL of product image (typically under /uploads or external CDN). */
  private String imageUrl;

  /** Longer text describing product features. */
  private String description;

  /** Unit price in store currency. Must be non-negative. */
  private double price;

  /** Available stock quantity. Must be non-negative. */
  private int stock;

  /** Order items linked to this product. */
  @OneToMany(mappedBy = "produit", cascade = jakarta.persistence.CascadeType.REMOVE, orphanRemoval = true)
  private List<CommandeItem> commandeItems;

    // Constructeur utilisé pour les tests
    public Produit(Long id, Categorie categorie, String name, String imageUrl, String description, double price, int stock) {
      this.id = id;
      this.categorie = categorie;
      this.name = name;
      this.imageUrl = imageUrl;
      this.description = description;
      this.price = price;
      this.stock = stock;
    }
}
