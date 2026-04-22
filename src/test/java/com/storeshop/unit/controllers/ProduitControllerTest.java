package com.storeshop.unit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.storeshop.controllers.ProduitController;
import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.services.CategorieService;
import com.storeshop.services.ProduitService;
import java.util.Arrays;
import java.util.List;
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
 * Tests unitaires pour {@link ProduitController}.
 * Utilise JUnit 5 et Mockito pour valider la gestion des produits (CRUD) et l'intégration des catégories.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller Produit")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class ProduitControllerTest {

  @Mock private ProduitService produitService;

  @Mock private CategorieService categorieService;

  @Mock private Model model;

  @InjectMocks private ProduitController produitController;

  private MockMvc mockMvc;

  private Produit produit1;
  private Produit produit2;
  private Categorie categorie1;
  private Categorie categorie2;

  /**
   * Initialisation des données de test avant chaque exécution.
   */
  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(produitController).build();

    categorie1 = new Categorie(1L, null, "Electronique");
    categorie2 = new Categorie(2L, null, "Informatique");

    produit1 = new Produit();
    produit1.setId(1L);
    produit1.setName("Smartphone");
    produit1.setCategorie(categorie1);
    produit1.setImageUrl("https://example.com/smartphone.jpg");
    produit1.setDescription("Telephone portable S25 Ultra");
    produit1.setPrice(800);
    produit1.setStock(100);

    produit2 = new Produit();
    produit2.setId(2L);
    produit2.setName("Ordinateur portable");
    produit2.setCategorie(categorie2);
    produit2.setImageUrl("https://example.com/laptop.jpg");
    produit2.setDescription("PC portable 15 pouces");
    produit2.setPrice(899.99);
    produit2.setStock(5);
  }

  /**
   * Teste l'affichage de la liste paginée des produits.
   */
  @Test
  @DisplayName("Test index - Liste des produits")
  void testIndex() {
    List<Produit> produits = Arrays.asList(produit1, produit2);
    Page<Produit> page = new PageImpl<>(produits);
    when(produitService.searchProduits(anyString(), anyInt(), anyInt())).thenReturn(page);

    String view = produitController.index(model, 0, 5, "");

    assertEquals("produit/ListeProduit", view);
    verify(model).addAttribute("ListeProduit", produits);
    verify(model).addAttribute("currentPage", 0);
  }

  /**
   * Teste la suppression d'un produit.
   */
  @Test
  @DisplayName("Test Supprimer Produit")
  void testDeleteProduit() {
    doNothing().when(produitService).deleteProduit(1L);

    String redirect = produitController.deleteProduit(1L, 0, "");

    assertEquals("redirect:/admin/produits?page=0&search=", redirect);
    verify(produitService).deleteProduit(1L);
  }

  /**
   * Teste l'affichage du formulaire d'édition.
   */
  @Test
  @DisplayName("Test showEditForm")
  void testShowEditForm() {
    when(produitService.getProduitById(1L)).thenReturn(produit1);
    when(categorieService.getAllCategories()).thenReturn(Arrays.asList(categorie1, categorie2));

    String view = produitController.showEditForm(1L, "", model);

    assertEquals("produit/editProduit", view);
    verify(model).addAttribute("produit", produit1);
    verify(model).addAttribute(eq("categories"), anyList());
  }

  /**
   * Teste l'enregistrement d'un produit existant.
   */
  @Test
  @DisplayName("Test saveProduit")
  void testSaveProduit() {
    when(categorieService.getCategorieById(1L)).thenReturn(categorie1);
    when(produitService.saveProduit(any(Produit.class))).thenReturn(produit1);

    String redirect = produitController.saveProduit(produit1, 1L, "", null);

    assertEquals("redirect:/admin/produits?search=", redirect);
    verify(categorieService).getCategorieById(1L);
    verify(produitService).saveProduit(produit1);
  }

  /**
   * Teste l'affichage du formulaire d'ajout.
   */
  @Test
  @DisplayName("Test showAddForm")
  void testShowAddForm() {
    when(categorieService.getAllCategories()).thenReturn(Arrays.asList(categorie1, categorie2));

    String view = produitController.showAddForm("", model);

    assertEquals("produit/ajouterProduit", view);

    verify(model).addAttribute(eq("produit"), any(Produit.class));
    verify(model).addAttribute(eq("categories"), anyList());
  }

  /**
   * Teste la création d'un nouveau produit.
   */
  @Test
  @DisplayName("Test addProduit")
  void testAddProduit() {
    when(categorieService.getCategorieById(1L)).thenReturn(categorie1);
    when(produitService.saveProduit(any(Produit.class))).thenReturn(produit1);

    String redirect = produitController.addProduit(produit1, 1L, "", null);

    assertEquals("redirect:/admin/produits?search=", redirect);
    verify(categorieService).getCategorieById(1L);
    verify(produitService).saveProduit(produit1);
  }

  /**
   * Teste la route vers l'accueil de l'administration.
   */
  @Test
  @DisplayName("Test home")
  void testHome() {
    String redirect = produitController.home();
    assertEquals("redirect:/admin/dashboard", redirect);
  }

  /**
   * Teste la liste des produits via MockMvc.
   * 
   * @throws Exception En cas d'erreur MockMvc.
   */
  @Test
  @DisplayName("Test avec MockMvc ")
  void testIndexWithMockMvc() throws Exception {
    Page<Produit> page = new PageImpl<>(Arrays.asList(produit1));
    when(produitService.searchProduits(anyString(), anyInt(), anyInt())).thenReturn(page);

    mockMvc
        .perform(get("/admin/produits"))
        .andExpect(status().isOk())
        .andExpect(view().name("produit/ListeProduit"));
  }
}

