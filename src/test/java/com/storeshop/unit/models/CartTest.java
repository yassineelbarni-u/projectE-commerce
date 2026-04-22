package com.storeshop.unit.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.storeshop.models.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du modèle Cart")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class CartTest {

  @Test
  @DisplayName("addItem ignore les quantités nulles ou négatives")
  void addItemIgnoresNonPositiveQuantities() {
    Cart cart = new Cart();

    cart.addItem(1L, 0);
    cart.addItem(2L, -3);

    assertTrue(cart.getItems().isEmpty());
  }

  @Test
  @DisplayName("updateItem retire l'article si la quantité devient nulle")
  void updateItemRemovesItemWhenQuantityIsNonPositive() {
    Cart cart = new Cart();
    cart.addItem(5L, 2);

    cart.updateItem(5L, 0);

    assertFalse(cart.getItems().containsKey(5L));
  }

  @Test
  @DisplayName("removeItem et clear vident correctement le panier")
  void removeItemAndClearWorkAsExpected() {
    Cart cart = new Cart();
    cart.addItem(1L, 2);
    cart.addItem(2L, 1);

    cart.removeItem(1L);

    assertEquals(1, cart.getItems().size());
    assertTrue(cart.getItems().containsKey(2L));

    cart.clear();

    assertTrue(cart.isEmpty());
  }
}
