package com.storeshop.integration;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.entities.Role;
import com.storeshop.entities.User;
import com.storeshop.repositories.CategorieRepository;
import com.storeshop.repositories.CommandeRepository;
import com.storeshop.repositories.ProduitRepository;
import com.storeshop.repositories.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@ActiveProfiles("test")
/**
 * Type : Base Test d'Intégration
 */
@org.junit.jupiter.api.Tag("Integration")
abstract class IntegrationTestBase {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected UserRepository userRepository;

  @Autowired protected ProduitRepository produitRepository;

  @Autowired protected CategorieRepository categorieRepository;

  @Autowired protected CommandeRepository commandeRepository;

  @Autowired protected PasswordEncoder passwordEncoder;

  @BeforeEach
  void cleanDatabase() {
    commandeRepository.deleteAll();
    produitRepository.deleteAll();
    categorieRepository.deleteAll();
    userRepository.deleteAll();
  }

  protected User createUser(String username, String rawPassword, Role role) {
    User user =
        User.builder()
            .userId(UUID.randomUUID().toString())
            .username(username)
            .password(passwordEncoder.encode(rawPassword))
            .email(username + "@example.com")
            .role(role)
            .build();
    return userRepository.save(user);
  }

  protected Categorie createCategory(String name) {
    return categorieRepository.save(new Categorie(null, null, name));
  }

  protected Produit createProduct(
      Categorie categorie, String name, String description, double price, int stock) {
    Produit produit = new Produit();
    produit.setCategorie(categorie);
    produit.setName(name);
    produit.setDescription(description);
    produit.setPrice(price);
    produit.setStock(stock);
    produit.setImageUrl("");
    return produitRepository.save(produit);
  }
}
