package com.storeshop.controllers;

import com.storeshop.services.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@AllArgsConstructor
public class PublicModelAdvice {

  private final CartService cartService;

  @ModelAttribute("cartCount")
  public int cartCount(HttpSession session) {
    return cartService.getTotalQuantity(session);
  }
}
