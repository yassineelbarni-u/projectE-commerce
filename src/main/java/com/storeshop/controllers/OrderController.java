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
public class OrderController {

  private final AccountService accountService;
  private final CommandeService commandeService;
  private final CartService cartService;

  @GetMapping
  public String listOrders(Model model, Principal principal) {
    if (principal == null) {
      return "redirect:/login";
    }
    User user = accountService.loadUserByUsername(principal.getName());
    model.addAttribute("orders", commandeService.listUserOrders(user));
    return "public/commandes";
  }

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
