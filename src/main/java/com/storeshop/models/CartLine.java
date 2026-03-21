package com.storeshop.models;

import com.storeshop.entities.Produit;

public class CartLine {

  private final Produit produit;
  private final int quantity;
  private final double lineTotal;

  public CartLine(Produit produit, int quantity, double lineTotal) {
    this.produit = produit;
    this.quantity = quantity;
    this.lineTotal = lineTotal;
  }

  public Produit getProduit() {
    return produit;
  }

  public int getQuantity() {
    return quantity;
  }

  public double getLineTotal() {
    return lineTotal;
  }
}
