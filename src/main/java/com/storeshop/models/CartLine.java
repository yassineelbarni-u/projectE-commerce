package com.storeshop.models;

import com.storeshop.entities.Produit;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * View model for one rendered cart row.
 */
@AllArgsConstructor
@Data
public class CartLine {

  /** Product metadata used by the UI. */
  private final Produit produit;

  /** Quantity selected by the user. */
  private final int quantity;

  /** Row subtotal (product price * quantity). */
  private final double lineTotal;
}
