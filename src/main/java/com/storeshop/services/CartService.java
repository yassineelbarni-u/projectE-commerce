package com.storeshop.services;

import com.storeshop.models.Cart;
import com.storeshop.models.CartLine;
import jakarta.servlet.http.HttpSession;
import java.util.List;

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
