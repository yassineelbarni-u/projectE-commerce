package com.storeshop.controllers;

import com.storeshop.services.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@AllArgsConstructor
/**
 * Global model contributor for server-rendered pages.
 *
 * <p>{@code @ControllerAdvice} runs alongside regular controllers and can inject shared values
 * into every template model.
 */
public class PublicModelAdvice {

  private final CartService cartService;

  /**
   * Adds total cart quantity to the model as {@code cartCount}.
   *
   * @param session current HTTP session
   * @return total quantity of items currently in cart
   */
  @ModelAttribute("cartCount")
  public int cartCount(HttpSession session) {
    return cartService.getTotalQuantity(session);
  }
}
