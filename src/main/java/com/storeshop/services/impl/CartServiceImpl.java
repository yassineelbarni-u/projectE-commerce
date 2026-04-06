package com.storeshop.services.impl;

import com.storeshop.entities.Produit;
import com.storeshop.models.Cart;
import com.storeshop.models.CartLine;
import com.storeshop.services.CartService;
import com.storeshop.services.ProduitService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Session-scoped cart: products are keyed by id in {@link Cart}; lines are built on demand with
 * fresh prices from {@link ProduitService}.
 */
@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

  /**
   * Session attribute name; keep stable so other layers can document the same contract if needed.
   */
  private static final String CART_SESSION_KEY = "CART";

  private final ProduitService produitService;

  /**
   * Retrieves the cart from session or creates one when absent.
   *
   * @param session current HTTP session
   * @return session-bound cart instance
   */
  @Override
  public Cart getCart(HttpSession session) {
    Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
    if (cart == null) {
      cart = new Cart();
      session.setAttribute(CART_SESSION_KEY, cart);
    }
    return cart;
  }

  /**
   * Adds quantity to a cart line.
   *
   * @param session current HTTP session
   * @param produitId product id
   * @param quantity quantity to add
   */
  @Override
  public void addItem(HttpSession session, Long produitId, int quantity) {
    Cart cart = getCart(session);
    cart.addItem(produitId, quantity);
  }

  /**
   * Replaces quantity for a cart line.
   *
   * @param session current HTTP session
   * @param produitId product id
   * @param quantity new quantity
   */
  @Override
  public void updateItem(HttpSession session, Long produitId, int quantity) {
    Cart cart = getCart(session);
    cart.updateItem(produitId, quantity);
  }

  /**
   * Removes one cart line.
   *
   * @param session current HTTP session
   * @param produitId product id
   */
  @Override
  public void removeItem(HttpSession session, Long produitId) {
    Cart cart = getCart(session);
    cart.removeItem(produitId);
  }

  /**
   * Clears the entire cart.
   *
   * @param session current HTTP session
   */
  @Override
  public void clear(HttpSession session) {
    Cart cart = getCart(session);
    cart.clear();
  }

  /**
   * Computes total item count across all lines.
   *
   * @param session current HTTP session
   * @return sum of quantities
   */
  @Override
  public int getTotalQuantity(HttpSession session) {
    Cart cart = getCart(session);
    int total = 0;
    for (Integer quantity : cart.getItems().values()) {
      total += quantity;
    }
    return total;
  }

  /**
   * Expands cart entries into render-ready lines.
   *
   * @param cart cart containing product ids and quantities
   * @return list of lines with current product data and line totals
   * @throws RuntimeException if a referenced product does not exist
   */
  @Override
  public List<CartLine> buildLines(Cart cart) {
    List<CartLine> lines = new ArrayList<>();
    for (Map.Entry<Long, Integer> entry : cart.getItems().entrySet()) {
      Produit produit = produitService.getProduitById(entry.getKey());

      int quantity = entry.getValue();
      double price = produit.getPrice();

      double lineTotal = price * quantity;
      lines.add(new CartLine(produit, quantity, lineTotal));
    }
    return lines;
  }

  /**
   * Computes grand total from prepared lines.
   *
   * @param lines cart lines
   * @return sum of line totals
   */
  @Override
  public double computeTotal(List<CartLine> lines) {
    double total = 0;
    for (CartLine line : lines) {
      total += line.getLineTotal();
    }
    return total;
  }
}
