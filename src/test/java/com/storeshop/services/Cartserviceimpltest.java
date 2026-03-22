package com.storeshop.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.models.Cart;
import com.storeshop.models.CartLine;
import com.storeshop.services.impl.CartServiceImpl;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service CartServiceImpl")
class CartServiceImplTest {

  @Mock private ProduitService produitService;
  @Mock private HttpSession session;

  @InjectMocks private CartServiceImpl cartService;

  private Produit produit1;
  private Produit produit2;
  private Cart cart;

  @BeforeEach
  void setUp() {
    Categorie categorie = new Categorie(1L, null, "Electronique");

    produit1 = new Produit();
    produit1.setId(1L);
    produit1.setName("Smartphone");
    produit1.setPrice(800.0);
    produit1.setStock(10);
    produit1.setCategorie(categorie);

    produit2 = new Produit();
    produit2.setId(2L);
    produit2.setName("Laptop");
    produit2.setPrice(1200.0);
    produit2.setStock(5);
    produit2.setCategorie(categorie);

    cart = new Cart();
  }

  @Test
  @DisplayName("getCart - crée un nouveau panier si session vide")
  void testGetCart_CreatesNewCart() {
    when(session.getAttribute("CART")).thenReturn(null);

    Cart result = cartService.getCart(session);

    assertNotNull(result);
    verify(session).setAttribute(eq("CART"), any(Cart.class));
  }

  @Test
  @DisplayName("getCart - retourne le panier existant depuis la session")
  void testGetCart_ReturnsExistingCart() {
    when(session.getAttribute("CART")).thenReturn(cart);

    Cart result = cartService.getCart(session);

    assertSame(cart, result);
    verify(session, never()).setAttribute(any(), any());
  }

  @Test
  @DisplayName("addItem - ajoute un produit au panier")
  void testAddItem() {
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.addItem(session, 1L, 2);

    assertEquals(2, cart.getItems().get(1L));
  }

  @Test
  @DisplayName("addItem - accumule la quantité si produit déjà présent")
  void testAddItem_AccumulatesQuantity() {
    cart.addItem(1L, 3);
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.addItem(session, 1L, 2);

    assertEquals(5, cart.getItems().get(1L));
  }

  @Test
  @DisplayName("updateItem - met à jour la quantité d'un produit")
  void testUpdateItem() {
    cart.addItem(1L, 3);
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.updateItem(session, 1L, 5);

    assertEquals(5, cart.getItems().get(1L));
  }

  @Test
  @DisplayName("removeItem - supprime un produit du panier")
  void testRemoveItem() {
    cart.addItem(1L, 2);
    cart.addItem(2L, 1);
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.removeItem(session, 1L);

    assertFalse(cart.getItems().containsKey(1L));
    assertTrue(cart.getItems().containsKey(2L));
  }

  @Test
  @DisplayName("clear - vide le panier")
  void testClear() {
    cart.addItem(1L, 2);
    cart.addItem(2L, 3);
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.clear(session);

    assertTrue(cart.getItems().isEmpty());
  }

  @Test
  @DisplayName("getTotalQuantity - calcule la quantité totale")
  void testGetTotalQuantity() {
    cart.addItem(1L, 3);
    cart.addItem(2L, 2);
    when(session.getAttribute("CART")).thenReturn(cart);

    int total = cartService.getTotalQuantity(session);

    assertEquals(5, total);
  }

  @Test
  @DisplayName("getTotalQuantity - retourne 0 si panier vide")
  void testGetTotalQuantity_EmptyCart() {
    when(session.getAttribute("CART")).thenReturn(cart);

    int total = cartService.getTotalQuantity(session);

    assertEquals(0, total);
  }

  @Test
  @DisplayName("buildLines - construit les lignes du panier avec prix et total")
  void testBuildLines() {
    cart.addItem(1L, 2);
    cart.addItem(2L, 1);
    when(produitService.getProduitById(1L)).thenReturn(produit1);
    when(produitService.getProduitById(2L)).thenReturn(produit2);

    List<CartLine> lines = cartService.buildLines(cart);

    assertEquals(2, lines.size());
    double totalLine1 = lines.stream()
        .filter(l -> l.getProduit().getId().equals(1L))
        .mapToDouble(CartLine::getLineTotal)
        .findFirst().orElse(0);
    assertEquals(1600.0, totalLine1, 0.01);
  }

  @Test
  @DisplayName("buildLines - retourne liste vide si panier vide")
  void testBuildLines_EmptyCart() {
    List<CartLine> lines = cartService.buildLines(cart);
    assertTrue(lines.isEmpty());
  }

  @Test
  @DisplayName("computeTotal - calcule le total du panier")
  void testComputeTotal() {
    cart.addItem(1L, 2);
    cart.addItem(2L, 1);
    when(produitService.getProduitById(1L)).thenReturn(produit1);
    when(produitService.getProduitById(2L)).thenReturn(produit2);

    List<CartLine> lines = cartService.buildLines(cart);
    double total = cartService.computeTotal(lines);

    assertEquals(2800.0, total, 0.01);
  }

  @Test
  @DisplayName("computeTotal - retourne 0 pour une liste vide")
  void testComputeTotal_EmptyList() {
    double total = cartService.computeTotal(List.of());
    assertEquals(0.0, total, 0.01);
  }
}