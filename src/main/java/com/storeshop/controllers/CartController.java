package com.storeshop.controllers;

import com.storeshop.models.Cart;
import com.storeshop.models.CartLine;
import com.storeshop.services.CartService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/panier")
@AllArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping
  public String viewCart(Model model, HttpSession session) {
    // get cart from session and build lines to display in the view
    Cart cart = cartService.getCart(session);
    List<CartLine> lines = cartService.buildLines(cart);
    model.addAttribute("lines", lines);
    model.addAttribute("total", cartService.computeTotal(lines));
    return "public/panier";
  }

  @PostMapping("/add")
  public String addItem(
      @RequestParam(name = "produitId") Long produitId,
      @RequestParam(name = "quantity", defaultValue = "1") int quantity,
      @RequestParam(name = "returnUrl", required = false) String returnUrl,
      HttpSession session) {

    cartService.addItem(session, produitId, quantity);
    if (returnUrl != null && returnUrl.startsWith("/")) {
      return "redirect:" + returnUrl;
    }
    return "redirect:/panier";
  }

  @PostMapping("/update")
  public String updateItem(
      @RequestParam(name = "produitId") Long produitId,
      @RequestParam(name = "quantity") int quantity,
      HttpSession session) {

    cartService.updateItem(session, produitId, quantity);
    return "redirect:/panier";
  }

  @PostMapping("/remove")
  public String removeItem(
      @RequestParam(name = "produitId") Long produitId, HttpSession session) {

    cartService.removeItem(session, produitId);
    return "redirect:/panier";
  }

  @PostMapping("/clear")
  public String clearCart(HttpSession session) {
    cartService.clear(session);
    return "redirect:/panier";
  }
}
