package com.storeshop.controllers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.services.CategorieService;
import com.storeshop.services.ProduitService;

/**
 * Tests unitaires pour ProduitController
 * Utilise JUnit 5 (Jupiter) et Mockito
 */

// active Mockito avec junit dans ce test
@ExtendWith(MockitoExtension.class)

@DisplayName("Tests du Controller Produit")
class ProduitControllerTest {

    // Mock des dépendances pour isoler le controller
    @Mock
    private ProduitService produitService;
    
    @Mock
    private CategorieService categorieService;
    
    // Mock du model pour verifier les attributs ajoutes
    @Mock
    private Model model;
    
    @InjectMocks
    private ProduitController produitController;

    // MockMvc pour tester les endpoints HTTP
    private MockMvc mockMvc;

    private Produit produit1;
    private Produit produit2;
    private Categorie categorie1;
    private Categorie categorie2;


    @BeforeEach
    void setUp() {
        // Initialiser MockMvc avec le controller a tester
        mockMvc = MockMvcBuilders.standaloneSetup(produitController).build();
        
        // Créer les catégories pour les tests
        categorie1 = new Categorie(1L, null, "Electronique");
        categorie2 = new Categorie(2L, null, "Informatique");
        
        // Créer les produits avec la nouvelle structure
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

    
    @Test
    @DisplayName("Test index - Liste des produits")
    void testIndex() {

        // Arrange (Ou jai prepare mes donnees pour les informations)
        List<Produit> produits = Arrays.asList(produit1, produit2);
        Page<Produit> page = new PageImpl<>(produits);
        // simuler le comportement du service pour retourner la page de produits
        when(produitService.searchProduits(anyString(), anyInt(), anyInt())).thenReturn(page);

        // Act (quend jai execute la methode que je veux tester)
        String view = produitController.index(model, 0, 5, "");

        // assert (verifier le resultat)
        assertEquals("produit/ListeProduit", view);
       // verifier les donnees ajoutees au model
        verify(model).addAttribute("ListeProduit", produits);
        verify(model).addAttribute("currentPage", 0);
    }

    @Test
    @DisplayName("Test Supprimer Produit")
    void testDeleteProduit() {

        
        doNothing().when(produitService).deleteProduit(1L);
        
        String redirect = produitController.deleteProduit(1L, 0, "");

        assertEquals("redirect:/admin/produits?page=0&search=", redirect);
        verify(produitService).deleteProduit(1L);
    }

    @Test
    @DisplayName("Test showEditForm")
    void testShowEditForm() {

        // Arrange
        when(produitService.getProduitById(1L)).thenReturn(produit1);
        when(categorieService.getAllCategories()).thenReturn(Arrays.asList(categorie1, categorie2));

        // Act
        String view = produitController.showEditForm(1L, "", model);

        // Assert
        assertEquals("produit/editProduit", view);
        verify(model).addAttribute("produit", produit1);
        verify(model).addAttribute(eq("categories"), anyList());
    }

    @Test
    @DisplayName("Test saveProduit")
    void testSaveProduit() {

        // Arrange
        when(categorieService.getCategorieById(1L)).thenReturn(categorie1);
        when(produitService.saveProduit(any(Produit.class))).thenReturn(produit1);

        // Act
        String redirect = produitController.saveProduit(produit1, 1L, "", null);

        // Assert
        assertEquals("redirect:/admin/produits?search=", redirect);
        verify(categorieService).getCategorieById(1L);
        verify(produitService).saveProduit(produit1);
    }

    @Test
    @DisplayName("Test showAddForm")
    void testShowAddForm() {
        // Arrange
        when(categorieService.getAllCategories()).thenReturn(Arrays.asList(categorie1, categorie2));
        
        // Act
        String view = produitController.showAddForm("", model);

        // Assert
        assertEquals("produit/ajouterProduit", view);

        verify(model).addAttribute(eq("produit"), any(Produit.class));
        verify(model).addAttribute(eq("categories"), anyList());
    }

    @Test
    @DisplayName("Test addProduit")
    void testAddProduit() {
        // Arrange
        when(categorieService.getCategorieById(1L)).thenReturn(categorie1);
        when(produitService.saveProduit(any(Produit.class))).thenReturn(produit1);

        // Act
        String redirect = produitController.addProduit(produit1, 1L, "", null);

        // Assert
        assertEquals("redirect:/admin/produits?search=", redirect);
        verify(categorieService).getCategorieById(1L);
        verify(produitService).saveProduit(produit1);
    }

    @Test
    @DisplayName("Test home")
    void testHome() {
        String redirect = produitController.home();
        assertEquals("redirect:/admin/dashboard", redirect);
    }

    @Test
    @DisplayName("Test avec MockMvc ")
    void testIndexWithMockMvc() throws Exception {
        Page<Produit> page = new PageImpl<>(Arrays.asList(produit1));
        when(produitService.searchProduits(anyString(), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/admin/produits"))
                .andExpect(status().isOk())
                .andExpect(view().name("produit/ListeProduit"));
    }
}
