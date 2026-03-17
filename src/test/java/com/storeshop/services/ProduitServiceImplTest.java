package com.storeshop.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.repositories.ProduitRepository;
import com.storeshop.services.impl.ProduitServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service ProduitServiceImpl")
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private Produit produit1;
    private Produit produit2;
    private Categorie categorie;

    @BeforeEach
    void setUp() {
        categorie = new Categorie(1L, null, "Electronique");

        produit1 = new Produit();
        produit1.setId(1L);
        produit1.setName("Smartphone");
        produit1.setCategorie(categorie);
        produit1.setDescription("Telephone haut de gamme");
        produit1.setPrice(599.99);
        produit1.setStock(10);

        produit2 = new Produit();
        produit2.setId(2L);
        produit2.setName("Laptop");
        produit2.setCategorie(categorie);
        produit2.setDescription("PC portable");
        produit2.setPrice(899.99);
        produit2.setStock(5);
    }

    @Test
    @DisplayName("searchProduits ")
    void testSearchProduits() {
        List<Produit> produits = Arrays.asList(produit1, produit2);
        Page<Produit> expectedPage = new PageImpl<>(produits);
        when(produitRepository.searchProduits(eq("Smart"), any(PageRequest.class))).thenReturn(expectedPage);

        Page<Produit> result = produitService.searchProduits("Smart", 0, 5);

        assertEquals(2, result.getContent().size());
        verify(produitRepository).searchProduits(eq("Smart"), any(PageRequest.class));
    }

    @Test
    @DisplayName("searchProduits ")
    void testSearchProduits_Empty() {
        Page<Produit> emptyPage = new PageImpl<>(List.of());
        when(produitRepository.searchProduits(eq("xyz"), any(PageRequest.class))).thenReturn(emptyPage);

        Page<Produit> result = produitService.searchProduits("xyz", 0, 5);

        assertTrue(result.getContent().isEmpty());
    }


    @Test
    @DisplayName("getProduitById ")
    void testGetProduitById_Found() {
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit1));

        Produit result = produitService.getProduitById(1L);

        assertNotNull(result);
        assertEquals("Smartphone", result.getName());
        assertEquals(599.99, result.getPrice());
    }

    @Test
    @DisplayName("getProduitById ")
    void testGetProduitById_NotFound() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> produitService.getProduitById(99L));

        assertTrue(exception.getMessage().contains("Produit non trouve"));
    }


    @Test
    @DisplayName("saveProduit - Sauvegarde reussie")
    void testSaveProduit_Success() {
        when(produitRepository.save(produit1)).thenReturn(produit1);

        Produit result = produitService.saveProduit(produit1);

        assertNotNull(result);
        assertEquals("Smartphone", result.getName());
        verify(produitRepository).save(produit1);
    }

    @Test
    @DisplayName("saveProduit - echec si le nom est vide")
    void testSaveProduit_EmptyName() {
        Produit invalidProduit = new Produit();
        invalidProduit.setName("");
        invalidProduit.setPrice(10.0);
        invalidProduit.setStock(5);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> produitService.saveProduit(invalidProduit));

        assertEquals("Le nom du produit ne peut pas etre vide", exception.getMessage());
        verify(produitRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveProduit - Échec si le nom est null")
    void testSaveProduit_NullName() {
        Produit invalidProduit = new Produit();
        invalidProduit.setName(null);
        invalidProduit.setPrice(10.0);
        invalidProduit.setStock(5);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> produitService.saveProduit(invalidProduit));

        assertEquals("Le nom du produit ne peut pas etre vide", exception.getMessage());
    }

    @Test
    @DisplayName("saveProduit - Echec si le prix est negatif")
    void testSaveProduit_NegativePrice() {
        Produit invalidProduit = new Produit();
        invalidProduit.setName("Test");
        invalidProduit.setPrice(-10.0);
        invalidProduit.setStock(5);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> produitService.saveProduit(invalidProduit));

        assertEquals("Le prix ne peut pas être negatif", exception.getMessage());
    }

    @Test
    @DisplayName("saveProduit - Echec si le stock est negatif")
    void testSaveProduit_NegativeStock() {
        Produit invalidProduit = new Produit();
        invalidProduit.setName("Test");
        invalidProduit.setPrice(10.0);
        invalidProduit.setStock(-1);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> produitService.saveProduit(invalidProduit));

        assertEquals("Le stock ne peut pas etre negatif", exception.getMessage());
    }


    @Test
    @DisplayName("deleteProduit - Suppression reussie")
    void testDeleteProduit_Success() {
        when(produitRepository.existsById(1L)).thenReturn(true);
        doNothing().when(produitRepository).deleteById(1L);

        assertDoesNotThrow(() -> produitService.deleteProduit(1L));

        verify(produitRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduit - Echec si le produit n'existe pas")
    void testDeleteProduit_NotFound() {
        when(produitRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> produitService.deleteProduit(99L));

        assertTrue(exception.getMessage().contains("Produit non trouve"));
        verify(produitRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("produitExists - Retourne true si le produit existe")
    void testProduitExists_True() {
        when(produitRepository.existsById(1L)).thenReturn(true);

        assertTrue(produitService.produitExists(1L));
    }

    @Test
    @DisplayName("produitExists - Retourne false si le produit n'existe pas")
    void testProduitExists_False() {
        when(produitRepository.existsById(99L)).thenReturn(false);

        assertFalse(produitService.produitExists(99L));
    }
}
