package com.storeshop.unit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.storeshop.controllers.PublicModelAdvice;
import com.storeshop.services.CartService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du ControllerAdvice PublicModelAdvice")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class PublicModelAdviceTest {

  @Mock private CartService cartService;
  @Mock private HttpSession session;

  @Test
  @DisplayName("cartCount expose la quantité totale d'articles du panier")
  void cartCountReturnsQuantityFromCartService() {
    when(cartService.getTotalQuantity(session)).thenReturn(7);

    PublicModelAdvice advice = new PublicModelAdvice(cartService);

    int count = advice.cartCount(session);

    assertEquals(7, count);
    verify(cartService).getTotalQuantity(session);
  }
}
