package com.storeshop.unit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.storeshop.controllers.LoginController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Tests unitaires pour le contrôleur {@link LoginController}.
 * Vérifie que les routes de connexion renvoient les vues appropriées.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller LoginController")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class LoginControllerTest {

  @InjectMocks private LoginController loginController;

  private MockMvc mockMvc;

  /**
   * Initialisation de l'environnement de test MockMvc.
   */
  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
  }

  /**
   * Teste l'accès direct à la méthode du contrôleur.
   */
  @Test
  @DisplayName("Test login - Retourne la vue login")
  void testLogin() {
    String view = loginController.login();

    assertEquals("login/login", view);
  }

  /**
   * Teste la route /login via MockMvc.
   * 
   * @throws Exception En cas d'erreur lors de l'exécution de la requête.
   */
  @Test
  @DisplayName("Test login avec MockMvc - Status OK")
  void testLoginWithMockMvc() throws Exception {
    mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login/login"));
  }
}
