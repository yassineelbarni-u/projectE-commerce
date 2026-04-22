package com.storeshop.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

/**
 * Type : Test d'Intégration
 */
@org.junit.jupiter.api.Tag("Integration")
class CartFlowIntegrationTest extends IntegrationTestBase {

  @Test
  void addUpdateRemoveAndClearCartPersistAcrossSession() throws Exception {
    User client = createUser("client-cart", "secret123", Role.CLIENT);
    Categorie books = createCategory("Books");
    Produit produit = createProduct(books, "Java Puzzlers", "Tricky Java book", 42.50, 10);
    MockHttpSession session = new MockHttpSession();

    mockMvc
        .perform(
            post("/panier/add")
                .with(user(client.getUsername()).roles("CLIENT"))
                .with(csrf())
                .session(session)
                .param("produitId", produit.getId().toString())
                .param("quantity", "2"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier"));

    mockMvc
        .perform(get("/panier").with(user(client.getUsername()).roles("CLIENT")).session(session))
        .andExpect(status().isOk())
        .andExpect(view().name("public/panier"))
        .andExpect(content().string(containsString("Java Puzzlers")))
        .andExpect(content().string(containsString("85.00")));

    mockMvc
        .perform(
            post("/panier/update")
                .with(user(client.getUsername()).roles("CLIENT"))
                .with(csrf())
                .session(session)
                .param("produitId", produit.getId().toString())
                .param("quantity", "3"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier"));

    mockMvc
        .perform(get("/").with(user(client.getUsername()).roles("CLIENT")).session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("cartCount", 3));

    mockMvc
        .perform(
            post("/panier/remove")
                .with(user(client.getUsername()).roles("CLIENT"))
                .with(csrf())
                .session(session)
                .param("produitId", produit.getId().toString()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier"));

    mockMvc
        .perform(get("/panier").with(user(client.getUsername()).roles("CLIENT")).session(session))
        .andExpect(status().isOk())
        .andExpect(content().string(not(containsString("Java Puzzlers"))));

    mockMvc
        .perform(
            post("/panier/clear")
                .with(user(client.getUsername()).roles("CLIENT"))
                .with(csrf())
                .session(session))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier"));

    mockMvc
        .perform(get("/home").with(user(client.getUsername()).roles("CLIENT")).session(session))
        .andExpect(status().isOk())
        .andExpect(model().attribute("cartCount", 0));
  }

  @Test
  void addItemUsesSafeRelativeReturnUrlOnly() throws Exception {
    User client = createUser("client-return-url", "secret123", Role.CLIENT);
    Categorie books = createCategory("Books");
    Produit produit = createProduct(books, "Effective Java", "Best practices", 50.00, 6);

    mockMvc
        .perform(
            post("/panier/add")
                .with(user(client.getUsername()).roles("CLIENT"))
                .with(csrf())
                .param("produitId", produit.getId().toString())
                .param("returnUrl", "/produits/detail?id=" + produit.getId()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/produits/detail?id=" + produit.getId()));

    mockMvc
        .perform(
            post("/panier/add")
                .with(user(client.getUsername()).roles("CLIENT"))
                .with(csrf())
                .param("produitId", produit.getId().toString())
                .param("returnUrl", "https://evil.example"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/panier"));
  }
}
