package com.storeshop.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One line inside an order.
 *
 * <p>Price values are copied at checkout time so historical orders stay consistent even if product
 * prices change later.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommandeItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Parent order owning this line. */
  @ManyToOne(optional = false)
  private Commande commande;

  /** Product that was purchased. */
  @ManyToOne(optional = false)
  private Produit produit;

  /** Quantity purchased for this product. */
  private int quantity;

  /** Unit price captured at checkout time. */
  private double unitPrice;

  /** Computed subtotal: unitPrice * quantity. */
  private double lineTotal;
}
