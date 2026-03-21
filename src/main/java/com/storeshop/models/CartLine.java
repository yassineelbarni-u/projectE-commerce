package com.storeshop.models;

import com.storeshop.entities.Produit;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor @Data
public class CartLine {

  private final Produit produit;
  private final int quantity;
  private final double lineTotal;

}
