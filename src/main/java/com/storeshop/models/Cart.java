package com.storeshop.models;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * In-session cart model keyed by product id.
 *
 * <p>The map value represents quantity. Price is intentionally not stored here; totals are
 * computed using current product data when rendering cart lines.
 */
public class Cart implements Serializable {

  /** productId -> quantity */
  private final Map<Long, Integer> items = new LinkedHashMap<>();

  /**
   * Returns all stored lines as map entries.
   *
   * @return mutable map of product ids to quantities
   */
  public Map<Long, Integer> getItems() {
    return items;
  }

  /**
   * Adds quantity to an existing line or creates a new line.
   *
   * @param produitId product id
   * @param quantity amount to add; values <= 0 are ignored
   */
  public void addItem(Long produitId, int quantity) {
    if (quantity <= 0) {
      return;
    }
    items.merge(produitId, quantity, Integer::sum);
  }

  /**
   * Replaces quantity for one line.
   *
   * @param produitId product id
   * @param quantity new quantity; values <= 0 remove the line
   */
  public void updateItem(Long produitId, int quantity) {
    if (quantity <= 0) {
      items.remove(produitId);
      return;
    }
    items.put(produitId, quantity);
  }

  /**
   * Removes one line from the cart.
   *
   * @param produitId product id
   */
  public void removeItem(Long produitId) {
    items.remove(produitId);
  }

  /** Clears all cart lines. */
  public void clear() {
    items.clear();
  }

  /**
   * Indicates whether the cart currently has no lines.
   *
   * @return true when map is empty
   */
  public boolean isEmpty() {
    return items.isEmpty();
  }
}
