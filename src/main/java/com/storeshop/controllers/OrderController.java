package com.storeshop.controllers;

import com.storeshop.entities.User;
import com.storeshop.models.Cart;
import com.storeshop.services.AccountService;
import com.storeshop.services.CartService;
import com.storeshop.services.CommandeService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/commandes")
@AllArgsConstructor
/**
 * Customer order controller.
 *
 * <p>Provides two actions: show the current user's order history and convert the current session
 * cart into a persisted order at checkout.
 */
public class OrderController {

  private final AccountService accountService;
  private final CommandeService commandeService;
  private final CartService cartService;

  /**
   * Displays all orders for the currently authenticated user.
   *
   * @param model MVC model that receives the orders list
   * @param principal authenticated identity provided by Spring Security
   * @return orders template, or login redirect when user is not authenticated
   */
  @GetMapping
  public String listOrders(Model model, Principal principal) {
    if (principal == null) {
      return "redirect:/login";
    }
    User user = accountService.loadUserByUsername(principal.getName());
    model.addAttribute("orders", commandeService.listUserOrders(user));
    return "public/commandes";
  }

  /**
   * Performs checkout for the logged-in user.
   *
   * <p>Reads items from the session cart, creates an order, then clears the cart when successful.
   *
   * @param session HTTP session containing the cart
   * @param principal authenticated identity
   * @return success redirect to order history, or login redirect when not authenticated
   */
  @PostMapping("/checkout")
  public String checkout(HttpSession session, Principal principal) {
    if (principal == null) {
      return "redirect:/login";
    }
    Cart cart = cartService.getCart(session);
    User user = accountService.loadUserByUsername(principal.getName());
    commandeService.createOrder(user, cart.getItems());
    cartService.clear(session);
    return "redirect:/commandes?success";
  }
}
