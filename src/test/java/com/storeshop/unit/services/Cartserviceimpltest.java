package com.storeshop.unit.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.models.Cart;
import com.storeshop.models.CartLine;
import com.storeshop.services.ProduitService;
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

/**
 * Tests unitaires pour {@link CartServiceImpl}.
 * Valide la gestion du panier en session : ajout, mise à jour, suppression et calculs totaux.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service CartServiceImpl")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class CartServiceImplTest {

  @Mock private ProduitService produitService;
  @Mock private HttpSession session;

  @InjectMocks private CartServiceImpl cartService;

  private Produit produit1;
  private Produit produit2;
  private Cart cart;

  /**
   * Initialisation des produits et du panier avant chaque test.
   */
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

  /**
   * Vérifie la création d'un panier si aucun n'est présent en session.
   */
  @Test
  @DisplayName("getCart - crée un nouveau panier si session vide")
  void testGetCart_CreatesNewCart() {
    when(session.getAttribute("CART")).thenReturn(null);

    Cart result = cartService.getCart(session);

    assertNotNull(result);
    verify(session).setAttribute(eq("CART"), any(Cart.class));
  }

  /**
   * Vérifie la récupération d'un panier existant.
   */
  @Test
  @DisplayName("getCart - retourne le panier existant depuis la session")
  void testGetCart_ReturnsExistingCart() {
    when(session.getAttribute("CART")).thenReturn(cart);

    Cart result = cartService.getCart(session);

    assertSame(cart, result);
    verify(session, never()).setAttribute(any(), any());
  }

  /**
   * Vérifie l'ajout d'un article.
   */
  @Test
  @DisplayName("addItem - ajoute un produit au panier")
  void testAddItem() {
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.addItem(session, 1L, 2);

    assertEquals(2, cart.getItems().get(1L));
  }

  /**
   * Vérifie l'accumulation des quantités pour un même article.
   */
  @Test
  @DisplayName("addItem - accumule la quantité si produit déjà présent")
  void testAddItem_AccumulatesQuantity() {
    cart.addItem(1L, 3);
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.addItem(session, 1L, 2);

    assertEquals(5, cart.getItems().get(1L));
  }

  /**
   * Vérifie la mise à jour forcée d'une quantité.
   */
  @Test
  @DisplayName("updateItem - met à jour la quantité d'un produit")
  void testUpdateItem() {
    cart.addItem(1L, 3);
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.updateItem(session, 1L, 5);

    assertEquals(5, cart.getItems().get(1L));
  }

  /**
   * Vérifie la suppression d'un article.
   */
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

  /**
   * Vérifie la vidange du panier.
   */
  @Test
  @DisplayName("clear - vide le panier")
  void testClear() {
    cart.addItem(1L, 2);
    cart.addItem(2L, 3);
    when(session.getAttribute("CART")).thenReturn(cart);

    cartService.clear(session);

    assertTrue(cart.getItems().isEmpty());
  }

  /**
   * Vérifie le calcul total des quantités d'articles.
   */
  @Test
  @DisplayName("getTotalQuantity - calcule la quantité totale")
  void testGetTotalQuantity() {
    cart.addItem(1L, 3);
    cart.addItem(2L, 2);
    when(session.getAttribute("CART")).thenReturn(cart);

    int total = cartService.getTotalQuantity(session);

    assertEquals(5, total);
  }

  /**
   * Vérifie le calcul d'un panier vide.
   */
  @Test
  @DisplayName("getTotalQuantity - retourne 0 si panier vide")
  void testGetTotalQuantity_EmptyCart() {
    when(session.getAttribute("CART")).thenReturn(cart);

    int total = cartService.getTotalQuantity(session);

    assertEquals(0, total);
  }

  /**
   * Vérifie la construction des lignes détaillées du panier (CartLine).
   */
  @Test
  @DisplayName("buildLines - construit les lignes du panier avec prix et total")
  void testBuildLines() {
    cart.addItem(1L, 2);
    cart.addItem(2L, 1);
    when(produitService.getProduitById(1L)).thenReturn(produit1);
    when(produitService.getProduitById(2L)).thenReturn(produit2);

    List<CartLine> lines = cartService.buildLines(cart);

    assertEquals(2, lines.size());
    double totalLine1 =
        lines.stream()
            .filter(l -> l.getProduit().getId().equals(1L))
            .mapToDouble(CartLine::getLineTotal)
            .findFirst()
            .orElse(0);
    assertEquals(1600.0, totalLine1, 0.01);
  }

  /**
   * Vérifie que buildLines retourne une liste vide pour un panier vide.
   */
  @Test
  @DisplayName("buildLines - retourne liste vide si panier vide")
  void testBuildLines_EmptyCart() {
    List<CartLine> lines = cartService.buildLines(cart);
    assertTrue(lines.isEmpty());
  }

  /**
   * Vérifie le calcul du montant total du panier.
   */
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

  /**
   * Vérifie que le total est 0 pour une liste de lignes vide.
   */
  @Test
  @DisplayName("computeTotal - retourne 0 pour une liste vide")
  void testComputeTotal_EmptyList() {
    double total = cartService.computeTotal(List.of());
    assertEquals(0.0, total, 0.01);
  }
}
