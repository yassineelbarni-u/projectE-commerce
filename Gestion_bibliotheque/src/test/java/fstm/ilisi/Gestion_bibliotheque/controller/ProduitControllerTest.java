package fstm.ilisi.Gestion_bibliotheque.controller;

import fstm.ilisi.Gestion_bibliotheque.entity.Categorie;
import fstm.ilisi.Gestion_bibliotheque.entity.Produit;
import fstm.ilisi.Gestion_bibliotheque.service.CategorieService;
import fstm.ilisi.Gestion_bibliotheque.service.ProduitService;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc = MockMvcBuilders.standaloneSetup(produitController).build();
        
        // Créer les catégories pour les tests
        categorie1 = new Categorie(1L, "Electronique");
        categorie2 = new Categorie(2L, "Informatique");
        
        // Créer les produits avec la nouvelle structure
        produit1 = new Produit();
        produit1.setId(1L);
        produit1.setNom("Smartphone");
        produit1.setCategorie(categorie1);
        produit1.setImageUrl("https://example.com/smartphone.jpg");
        produit1.setDescription("Téléphone haut de gamme");
        produit1.setPrix(599.99);
        produit1.setStock(10);
        
        produit2 = new Produit();
        produit2.setId(2L);
        produit2.setNom("Ordinateur portable");
        produit2.setCategorie(categorie2);
        produit2.setImageUrl("https://example.com/laptop.jpg");
        produit2.setDescription("PC portable 15 pouces");
        produit2.setPrix(899.99);
        produit2.setStock(5);
    }

    
    @Test
    @DisplayName("Test index - Liste des produits")
    void testIndex() {

        // Arrange
        List<Produit> produits = Arrays.asList(produit1, produit2);
        Page<Produit> page = new PageImpl<>(produits);

        // simulation du comportement du service pour retourner une page de produits
        when(produitService.searchProduits(anyString(), anyInt(), anyInt())).thenReturn(page);

        // appel de la méthode index du controller
        String view = produitController.index(model, 0, 5, "");

        // verification du nom de la vue retournée
        assertEquals("produit/ListeProduit", view);

        // verification que les attributs ont ete ajoutes au model
        verify(model).addAttribute("ListeProduit", produits);
        verify(model).addAttribute("currentPage", 0);
    }

    @Test
    @DisplayName("Test Supprimer Produit")
    void testDeleteProduit() {

        // Arrange (pour simuler la suppression d'un produit)
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
