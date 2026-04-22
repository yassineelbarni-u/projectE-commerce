package com.storeshop.integration;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Commande;
import com.storeshop.entities.Produit;
import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.models.Cart;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Type : Test d'Intégration
 */
@org.junit.jupiter.api.Tag("Integration")
class OrderCheckoutIntegrationTest extends IntegrationTestBase {

  @Test
  void checkoutCreatesOrderDecrementsStockAndClearsSessionCart() throws Exception {
    User client = createUser("client-checkout", "secret123", Role.CLIENT);
    Categorie books = createCategory("Books");
    Produit produit = createProduct(books, "Spring in Action", "Practical Spring", 39.99, 5);

    Cart cart = new Cart();
    cart.addItem(produit.getId(), 2);

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("CART", cart);

    MvcResult result =
        mockMvc
            .perform(
                post("/commandes/checkout")
                    .with(user(client.getUsername()).roles("CLIENT"))
                    .with(csrf())
                    .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/commandes?success"))
            .andReturn();

    List<Commande> commandes = commandeRepository.findAll();
    Produit updatedProduit = produitRepository.findById(produit.getId()).orElseThrow();
    Cart updatedCart = (Cart) result.getRequest().getSession().getAttribute("CART");

    assertEquals(1, commandes.size());
    assertEquals("VALIDEE", commandes.get(0).getStatus());
    assertEquals(79.98, commandes.get(0).getTotal(), 0.001);
    assertEquals(3, updatedProduit.getStock());
    assertTrue(updatedCart.getItems().isEmpty());
  }

  @Test
  void checkoutWithInsufficientStockRedirectsBackToCartWithoutPersistingOrder() throws Exception {
    User client = createUser("client-stock", "secret123", Role.CLIENT);
    Categorie books = createCategory("Books");
    Produit produit = createProduct(books, "Clean Code", "Classic book", 45.00, 1);

    Cart cart = new Cart();
    cart.addItem(produit.getId(), 2);

    MockHttpSession session = new MockHttpSession();
    session.setAttribute("CART", cart);

    mockMvc
        .perform(
            post("/commandes/checkout")
                .with(user(client.getUsername()).roles("CLIENT"))
                .with(csrf())
                .session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier?error=Stock insuffisant pour: Clean Code"));

    Produit unchangedProduit = produitRepository.findById(produit.getId()).orElseThrow();
    assertEquals(0, commandeRepository.count());
    assertEquals(1, unchangedProduit.getStock());
  }

  @Test
  void authenticatedUserCanViewOnlyTheirOrderHistory() throws Exception {
    User client = createUser("client-orders", "secret123", Role.CLIENT);
    User otherClient = createUser("other-client", "secret123", Role.CLIENT);
    Categorie books = createCategory("Books");
    Produit produit = createProduct(books, "Refactoring", "Improve design", 55.00, 9);

    commandeRepository.save(buildOrder(client, produit, 1, "VALIDEE"));
    commandeRepository.save(buildOrder(otherClient, produit, 2, "LIVREE"));

    mockMvc
        .perform(get("/commandes").with(user(client.getUsername()).roles("CLIENT")))
        .andExpect(status().isOk())
        .andExpect(view().name("public/commandes"))
        .andExpect(content().string(containsString("VALIDEE")))
        .andExpect(content().string(org.hamcrest.Matchers.not(containsString("LIVREE"))));
  }

  private Commande buildOrder(User user, Produit produit, int quantity, String status) {
    Commande commande = new Commande();
    commande.setUser(user);
    commande.setCreatedAt(java.time.LocalDateTime.now());
    commande.setStatus(status);
    commande.setTotal(produit.getPrice() * quantity);

    com.storeshop.entities.CommandeItem item = new com.storeshop.entities.CommandeItem();
    item.setProduit(produit);
    item.setQuantity(quantity);
    item.setUnitPrice(produit.getPrice());
    item.setLineTotal(produit.getPrice() * quantity);
    commande.addItem(item);
    return commande;
  }
}
