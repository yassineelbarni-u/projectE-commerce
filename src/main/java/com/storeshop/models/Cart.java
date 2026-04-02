package com.storeshop.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/*
    * Represents the shopping cart stored in the user's session. It contains a map of product IDs to quantities and provides methods to manipulate the cart.
 */

public class Cart implements Serializable {

  private final Map<Long, Integer> items = new LinkedHashMap<>();

  public Map<Long, Integer> getItems() {
    return items;
  }

  public void addItem(Long produitId, int quantity) {
    if (quantity <= 0) {
      return;
    }
    items.merge(produitId, quantity, Integer::sum);
  }

  public void updateItem(Long produitId, int quantity) {
    if (quantity <= 0) {
      items.remove(produitId);
      return;
    }
    items.put(produitId, quantity);
  }

  public void removeItem(Long produitId) {
    items.remove(produitId);
  }

  public void clear() {
    items.clear();
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }
}
