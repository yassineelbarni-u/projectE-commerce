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
/**
 * Session-cart controller used by public storefront pages.
 *
 * <p>Routes under {@code /panier} mutate a cart stored in {@link HttpSession}, then redirect to a
 * page where the updated cart state is visible.
 */
public class CartController {

  private final CartService cartService;

  /**
   * Displays cart lines and grand total.
   *
   * @param model MVC model receiving line items and total amount
   * @param session HTTP session where the cart is stored
   * @return cart template
   */
  @GetMapping
  public String viewCart(Model model, HttpSession session) {
    Cart cart = cartService.getCart(session);
    List<CartLine> lines = cartService.buildLines(cart);
    model.addAttribute("lines", lines);
    model.addAttribute("total", cartService.computeTotal(lines));
    return "public/panier";
  }

  /**
   * Adds a product to the cart.
   *
   * @param produitId product id
   * @param quantity quantity to add (defaults to 1)
   * @param returnUrl optional relative URL to return to after adding
   * @param session HTTP session where the cart is stored
   * @return redirect to a safe relative returnUrl or fallback cart page
   */
  @PostMapping("/add")
  public String addItem(
      @RequestParam(name = "produitId") Long produitId,
      @RequestParam(name = "quantity", defaultValue = "1") int quantity,
      @RequestParam(name = "returnUrl", required = false) String returnUrl,
      HttpSession session) {

    cartService.addItem(session, produitId, quantity);
    // Accept only site-relative return URLs to prevent open redirects.
    if (returnUrl != null && returnUrl.startsWith("/")) {
      return "redirect:" + returnUrl;
    }
    return "redirect:/panier";
  }

  /**
   * Sets a new quantity for an existing cart line.
   *
   * @param produitId product id to update
   * @param quantity new quantity; zero or negative removes the line per cart rules
   * @param session HTTP session where the cart is stored
   * @return redirect to cart page
   */
  @PostMapping("/update")
  public String updateItem(
      @RequestParam(name = "produitId") Long produitId,
      @RequestParam(name = "quantity") int quantity,
      HttpSession session) {

    cartService.updateItem(session, produitId, quantity);
    return "redirect:/panier";
  }

  /**
   * Removes one product line from the cart.
   *
   * @param produitId product id to remove
   * @param session HTTP session where the cart is stored
   * @return redirect to cart page
   */
  @PostMapping("/remove")
  public String removeItem(@RequestParam(name = "produitId") Long produitId, HttpSession session) {

    cartService.removeItem(session, produitId);
    return "redirect:/panier";
  }

  /**
   * Removes all lines from the current session cart.
   *
   * @param session HTTP session where the cart is stored
   * @return redirect to cart page
   */
  @PostMapping("/clear")
  public String clearCart(HttpSession session) {
    cartService.clear(session);
    return "redirect:/panier";
  }
}
