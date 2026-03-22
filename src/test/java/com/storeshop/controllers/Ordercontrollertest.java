package com.storeshop.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.storeshop.entities.Commande;
import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.models.Cart;
import com.storeshop.services.AccountService;
import com.storeshop.services.CartService;
import com.storeshop.services.CommandeService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller OrderController")
class OrderControllerTest {

  @Mock private AccountService accountService;
  @Mock private CommandeService commandeService;
  @Mock private CartService cartService;
  @Mock private Model model;
  @Mock private HttpSession session;
  @Mock private Principal principal;

  @InjectMocks private OrderController orderController;

  private MockMvc mockMvc;
  private User user;
  private Cart cart;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

    user = User.builder()
        .userId("uuid-1")
        .username("client1")
        .email("client1@gmail.com")
        .role(Role.CLIENT)
        .build();

    cart = new Cart();
    cart.addItem(1L, 2);
  }

  @Test
  @DisplayName("listOrders - retourne les commandes de l'utilisateur connecté")
  void testListOrders_Authenticated() {
    Commande c1 = new Commande();
    Commande c2 = new Commande();
    when(principal.getName()).thenReturn("client1");
    when(accountService.loadUserByUsername("client1")).thenReturn(user);
    when(commandeService.listUserOrders(user)).thenReturn(List.of(c1, c2));

    String view = orderController.listOrders(model, principal);

    assertEquals("public/commandes", view);
    verify(model).addAttribute("orders", List.of(c1, c2));
  }

  @Test
  @DisplayName("listOrders - redirige vers login si non authentifié")
  void testListOrders_NotAuthenticated() {
    String redirect = orderController.listOrders(model, null);
    assertEquals("redirect:/login", redirect);
    verify(accountService, never()).loadUserByUsername(any());
  }

  @Test
  @DisplayName("checkout - crée une commande et vide le panier")
  void testCheckout_Success() {
    when(principal.getName()).thenReturn("client1");
    when(cartService.getCart(session)).thenReturn(cart);
    when(accountService.loadUserByUsername("client1")).thenReturn(user);
    when(commandeService.createOrder(eq(user), any(Map.class))).thenReturn(new Commande());
    doNothing().when(cartService).clear(session);

    String redirect = orderController.checkout(session, principal);

    assertEquals("redirect:/commandes?success", redirect);
    verify(commandeService).createOrder(eq(user), any(Map.class));
    verify(cartService).clear(session);
  }

  @Test
  @DisplayName("checkout - redirige vers login si non authentifié")
  void testCheckout_NotAuthenticated() {
    String redirect = orderController.checkout(session, null);
    assertEquals("redirect:/login", redirect);
    verify(commandeService, never()).createOrder(any(), any());
  }

  @Test
  @DisplayName("listOrders - avec MockMvc redirige si non connecté")
  void testListOrders_MockMvc_Unauthenticated() throws Exception {
    mockMvc.perform(get("/commandes"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));
  }
}