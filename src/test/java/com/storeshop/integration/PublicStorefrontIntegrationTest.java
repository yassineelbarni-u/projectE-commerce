package com.storeshop.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import org.junit.jupiter.api.Test;

/**
 * Type : Test d'Intégration
 */
@org.junit.jupiter.api.Tag("Integration")
class PublicStorefrontIntegrationTest extends IntegrationTestBase {

  @Test
  void publicCatalogRendersPersistedProductsAndCategories() throws Exception {
    Categorie books = createCategory("Books");
    createProduct(books, "Java Handbook", "Spring and Java", 49.99, 10);

    mockMvc
        .perform(get("/produits"))
        .andExpect(status().isOk())
        .andExpect(view().name("public/home"))
        .andExpect(model().attributeExists("ListeProduit"))
        .andExpect(model().attributeExists("categories"))
        .andExpect(content().string(containsString("Java Handbook")))
        .andExpect(content().string(containsString("Books")));
  }

  @Test
  void publicCatalogAppliesSearchAndCategoryFilters() throws Exception {
    Categorie books = createCategory("Books");
    Categorie games = createCategory("Games");
    createProduct(books, "Java Handbook", "Spring and Java", 49.99, 10);
    createProduct(games, "Chess Board", "Classic strategy game", 29.99, 8);

    mockMvc
        .perform(get("/produits").param("search", "java").param("categorieId", books.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(view().name("public/home"))
        .andExpect(content().string(containsString("Java Handbook")))
        .andExpect(content().string(not(containsString("Chess Board"))));
  }

  @Test
  void productDetailDisplaysPersistedProduct() throws Exception {
    Categorie books = createCategory("Books");
    Produit produit = createProduct(books, "Domain-Driven Design", "Blue book", 59.99, 4);

    mockMvc
        .perform(get("/produits/detail").param("id", produit.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(view().name("public/detail-produit"))
        .andExpect(content().string(containsString("Domain-Driven Design")))
        .andExpect(content().string(containsString("Blue book")));
  }
}
