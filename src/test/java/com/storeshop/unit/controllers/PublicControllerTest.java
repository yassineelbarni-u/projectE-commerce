package com.storeshop.unit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.storeshop.controllers.PublicController;
import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.services.AccountService;
import com.storeshop.services.CategorieService;
import com.storeshop.services.ProduitService;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

/**
 * Tests unitaires pour {@link PublicController}.
 * Couvre l'affichage des produits pour les clients, la consultation des détails et l'inscription.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller PublicController")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class PublicControllerTest {

  @Mock private ProduitService produitService;

  @Mock private CategorieService categorieService;

  @Mock private AccountService accountService;

  @Mock private Model model;

  @InjectMocks private PublicController publicController;

  private MockMvc mockMvc;
  private Produit produit;
  private Categorie categorie;

  /**
   * Initialisation des données de test publiques.
   */
  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(publicController).build();
    categorie = new Categorie(1L, null, "Roman");
    produit =
        new Produit(1L, categorie, "Livre Java", "/uploads/book.jpg", "Guide complet", 199.0, 12);
  }

  /**
   * Teste l'affichage de la page d'accueil avec des produits.
   */
  @Test
  void testHome() {
    Page<Produit> page = new PageImpl<>(Arrays.asList(produit));
    when(produitService.searchProduitsPublic("java", 1L, 0, 8)).thenReturn(page);
    when(categorieService.getAllCategories()).thenReturn(Arrays.asList(categorie));

    String viewName = publicController.home(model, 0, 8, "java", 1L);

    assertEquals("public/home", viewName);
    verify(model).addAttribute("ListeProduit", page.getContent());
    verify(model).addAttribute("selectedCategorieId", 1L);
    verify(model).addAttribute("categories", Arrays.asList(categorie));
  }

  /**
   * Teste le cas d'un catalogue vide sans produits ni catégories.
   */
  @Test
  void testHome_EmptyCatalog() {
    Page<Produit> page = new PageImpl<>(Collections.emptyList());
    when(produitService.searchProduitsPublic("", null, 0, 8)).thenReturn(page);
    when(categorieService.getAllCategories()).thenReturn(Collections.emptyList());

    String viewName = publicController.home(model, 0, 8, "", null);

    assertEquals("public/home", viewName);
    verify(model).addAttribute("ListeProduit", Collections.emptyList());
  }

  /**
   * Teste l'affichage des détails d'un produit.
   */
  @Test
  void testShowProduitDetail() {
    when(produitService.getProduitById(1L)).thenReturn(produit);

    String viewName = publicController.showProduitDetail(1L, model);

    assertEquals("public/detail-produit", viewName);
    verify(model).addAttribute("produit", produit);
  }

  /**
   * Teste l'affichage du formulaire d'inscription.
   */
  @Test
  void testShowRegisterForm() {
    assertEquals("public/register", publicController.showRegisterForm());
  }

  /**
   * Teste une inscription réussie.
   */
  @Test
  void testRegister_Success() {
    String redirect = publicController.register("client1", "client1@gmail.com", "1234", "1234");
    assertEquals("redirect:/login?registered", redirect);
    verify(accountService).AddUser("client1", "1234", "client1@gmail.com", "1234");
  }

  /**
   * Teste une inscription échouée (ex: mots de passe non identiques).
   */
  @Test
  void testRegister_Error() {
    when(accountService.AddUser("client1", "1234", "client1@gmail.com", "0000"))
        .thenThrow(new RuntimeException("Passwords  not match"));

    String redirect = publicController.register("client1", "client1@gmail.com", "1234", "0000");

    assertTrue(redirect.startsWith("redirect:/register?error="));
    assertTrue(redirect.contains("username=client1"));
    assertTrue(redirect.contains("email=client1%40gmail.com"));
  }

  /**
   * Teste la page d'accueil avec MockMvc.
   * 
   * @throws Exception En cas d'erreur MockMvc.
   */
  @Test
  void testHomeWithMockMvc() throws Exception {
    Page<Produit> page = new PageImpl<>(Arrays.asList(produit));
    when(produitService.searchProduitsPublic(eq(""), eq(null), anyInt(), eq(8))).thenReturn(page);
    when(categorieService.getAllCategories()).thenReturn(Arrays.asList(categorie));

    mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("public/home"));
  }

  /**
   * Teste les détails du produit via MockMvc.
   * 
   * @throws Exception En cas d'erreur MockMvc.
   */
  @Test
  void testDetailWithMockMvc() throws Exception {
    when(produitService.getProduitById(1L)).thenReturn(produit);

    mockMvc
        .perform(get("/produits/detail").param("id", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("public/detail-produit"));
  }
}
