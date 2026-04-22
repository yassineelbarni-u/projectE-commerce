package com.storeshop.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.storeshop.controllers.CartController;
import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.models.Cart;
import com.storeshop.models.CartLine;
import com.storeshop.services.CartService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
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

/**
 * Tests unitaires pour {@link CartController}.
 * Valide les opérations sur le panier : affichage, ajout, mise à jour et suppression d'articles.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller CartController")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class CartControllerTest {

  @Mock private CartService cartService;
  @Mock private Model model;
  @Mock private HttpSession session;

  @InjectMocks private CartController cartController;

  private MockMvc mockMvc;
  private Cart cart;
  private CartLine cartLine1;
  private CartLine cartLine2;
  private Produit produit1;

  /**
   * Initialisation des objets de test (produits, lignes de panier).
   */
  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();

    cart = new Cart();
    Categorie cat = new Categorie(1L, null, "Electronique");

    produit1 = new Produit();
    produit1.setId(1L);
    produit1.setName("Smartphone");
    produit1.setPrice(800.0);
    produit1.setStock(10);
    produit1.setCategorie(cat);

    Produit produit2 = new Produit();
    produit2.setId(2L);
    produit2.setName("Laptop");
    produit2.setPrice(1200.0);
    produit2.setStock(5);
    produit2.setCategorie(cat);

    cartLine1 = new CartLine(produit1, 2, 1600.0);
    cartLine2 = new CartLine(produit2, 1, 1200.0);
  }

  /**
   * Teste l'affichage dynamique du panier.
   */
  @Test
  @DisplayName("viewCart - retourne la vue panier avec les lignes et le total")
  void testViewCart() {
    List<CartLine> lines = List.of(cartLine1, cartLine2);
    when(cartService.getCart(session)).thenReturn(cart);
    when(cartService.buildLines(cart)).thenReturn(lines);
    when(cartService.computeTotal(lines)).thenReturn(2800.0);

    String view = cartController.viewCart(model, session);

    assertEquals("public/panier", view);
    verify(model).addAttribute("lines", lines);
    verify(model).addAttribute("total", 2800.0);
  }

  /**
   * Teste l'accès au panier via MockMvc.
   * 
   * @throws Exception En cas d'erreur MockMvc.
   */
  @Test
  @DisplayName("viewCart - avec MockMvc retourne status 200")
  void testViewCart_MockMvc() throws Exception {
    MockHttpSession mockSession = new MockHttpSession();
    when(cartService.getCart(any(HttpSession.class))).thenReturn(cart);
    when(cartService.buildLines(cart)).thenReturn(List.of(cartLine1));
    when(cartService.computeTotal(anyList())).thenReturn(1600.0);

    mockMvc
        .perform(get("/panier").session(mockSession))
        .andExpect(status().isOk())
        .andExpect(view().name("public/panier"));
  }

  /**
   * Teste l'affichage d'un panier vide.
   */
  @Test
  @DisplayName("viewCart - panier vide retourne total 0")
  void testViewCart_EmptyCart() {
    when(cartService.getCart(session)).thenReturn(cart);
    when(cartService.buildLines(cart)).thenReturn(List.of());
    when(cartService.computeTotal(List.of())).thenReturn(0.0);

    String view = cartController.viewCart(model, session);

    assertEquals("public/panier", view);
    verify(model).addAttribute("total", 0.0);
  }

  /**
   * Teste l'ajout d'un article et la redirection par défaut vers le panier.
   */
  @Test
  @DisplayName("addItem - ajoute un produit et redirige vers le panier")
  void testAddItem_RedirectToCart() {
    doNothing().when(cartService).addItem(session, 1L, 2);

    String redirect = cartController.addItem(1L, 2, null, session);

    assertEquals("redirect:/panier", redirect);
    verify(cartService).addItem(session, 1L, 2);
  }

  /**
   * Teste l'ajout d'un article avec une URL de retour personnalisée.
   */
  @Test
  @DisplayName("addItem - redirige vers returnUrl si valide")
  void testAddItem_RedirectToReturnUrl() {
    doNothing().when(cartService).addItem(session, 1L, 1);

    String redirect = cartController.addItem(1L, 1, "/produits/detail?id=1", session);

    assertEquals("redirect:/produits/detail?id=1", redirect);
  }

  /**
   * Teste la sécurité contre les redirections externes.
   */
  @Test
  @DisplayName("addItem - ignore returnUrl externe (non relatif)")
  void testAddItem_IgnoresExternalReturnUrl() {
    doNothing().when(cartService).addItem(session, 1L, 1);

    String redirect = cartController.addItem(1L, 1, "https://malicious.com", session);

    assertEquals("redirect:/panier", redirect);
  }

  /**
   * Teste l'ajout via POST avec MockMvc.
   * 
   * @throws Exception En cas d'erreur MockMvc.
   */
  @Test
  @DisplayName("addItem - avec MockMvc redirige après POST")
  void testAddItem_MockMvc() throws Exception {
    MockHttpSession mockSession = new MockHttpSession();
    doNothing().when(cartService).addItem(any(HttpSession.class), eq(1L), eq(1));

    mockMvc
        .perform(
            post("/panier/add").param("produitId", "1").param("quantity", "1").session(mockSession))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier"));
  }

  @Test
  @DisplayName("updateItem - met à jour la quantité et redirige")
  void testUpdateItem() {
    doNothing().when(cartService).updateItem(session, 1L, 3);

    String redirect = cartController.updateItem(1L, 3, session);

    assertEquals("redirect:/panier", redirect);
    verify(cartService).updateItem(session, 1L, 3);
  }

  @Test
  @DisplayName("removeItem - supprime le produit et redirige")
  void testRemoveItem() {
    doNothing().when(cartService).removeItem(session, 1L);

    String redirect = cartController.removeItem(1L, session);

    assertEquals("redirect:/panier", redirect);
    verify(cartService).removeItem(session, 1L);
  }

  @Test
  @DisplayName("clearCart - vide le panier et redirige")
  void testClearCart() {
    doNothing().when(cartService).clear(session);

    String redirect = cartController.clearCart(session);

    assertEquals("redirect:/panier", redirect);
    verify(cartService).clear(session);
  }

  @Test
  @DisplayName("clearCart - avec MockMvc redirige après POST")
  void testClearCart_MockMvc() throws Exception {
    MockHttpSession mockSession = new MockHttpSession();
    doNothing().when(cartService).clear(any(HttpSession.class));

    mockMvc
        .perform(post("/panier/clear").session(mockSession))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier"));
  }
}
