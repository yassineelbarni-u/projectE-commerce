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

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

  private static final String CART_SESSION_KEY = "CART";

  private final ProduitService produitService;

  @Override
  public Cart getCart(HttpSession session) {
    // read attribut from session, if null create new cart and save in session
    Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
    if (cart == null) {
      cart = new Cart();
      session.setAttribute(CART_SESSION_KEY, cart);
    }
    return cart;
  }

  // add item to cart, if item already exists update quantity
  @Override
  public void addItem(HttpSession session, Long produitId, int quantity) {
    Cart cart = getCart(session);
    cart.addItem(produitId, quantity);
  }

  @Override
  public void updateItem(HttpSession session, Long produitId, int quantity) {
    Cart cart = getCart(session);
    cart.updateItem(produitId, quantity);
  }

  @Override
  public void removeItem(HttpSession session, Long produitId) {
    Cart cart = getCart(session);
    cart.removeItem(produitId);
  }

  @Override
  public void clear(HttpSession session) {
    Cart cart = getCart(session);
    cart.clear();
  }

  @Override
  public int getTotalQuantity(HttpSession session) {
    Cart cart = getCart(session);
    int total = 0;
    // sum all quantities in the cart
    for (Integer quantity : cart.getItems().values()) {
      total += quantity;
    }
    return total;
  }

// build cart lines with product details and line total
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

  @Override
  public double computeTotal(List<CartLine> lines) {
    double total = 0;
    for (CartLine line : lines) {
      total += line.getLineTotal();
    }
    return total;
  }
}
