package com.storeshop.services;

import com.storeshop.models.Cart;
import com.storeshop.models.CartLine;
import jakarta.servlet.http.HttpSession;
import java.util.List;
/*
    * Service for managing the shopping cart stored in the user's session. It provides methods to add, update, remove items,
 */
public interface CartService {

  Cart getCart(HttpSession session);

  void addItem(HttpSession session, Long produitId, int quantity);

  void updateItem(HttpSession session, Long produitId, int quantity);

  void removeItem(HttpSession session, Long produitId);

  void clear(HttpSession session);

  int getTotalQuantity(HttpSession session);

  List<CartLine> buildLines(Cart cart);

  double computeTotal(List<CartLine> lines);
}
