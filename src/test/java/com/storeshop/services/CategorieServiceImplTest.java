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
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.storeshop.entities.Categorie;
import com.storeshop.repositories.CategorieRepository;
import com.storeshop.services.impl.CategorieServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service CategorieServiceImpl")
class CategorieServiceImplTest {

    @Mock
    private CategorieRepository categorieRepository;

    @InjectMocks
    private CategorieServiceImpl categorieService;

    private Categorie categorie1;
    private Categorie categorie2;

    @BeforeEach
    void setUp() {
        categorie1 = new Categorie(1L, null, "Electronique");
        categorie2 = new Categorie(2L, null, "Informatique");
    }


    @Test
    @DisplayName("getAllCategories - Retourne toutes les catégories")
    void testGetAllCategories() {
        when(categorieRepository.findAll()).thenReturn(Arrays.asList(categorie1, categorie2));

        List<Categorie> result = categorieService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Electronique", result.get(0).getNom());
        assertEquals("Informatique", result.get(1).getNom());
        verify(categorieRepository).findAll();
    }

    @Test
    @DisplayName("getAllCategories - Retourne une liste vide")
    void testGetAllCategories_Empty() {
        when(categorieRepository.findAll()).thenReturn(List.of());

        List<Categorie> result = categorieService.getAllCategories();

        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("getCategorieById - Retourne la catégorie trouvée")
    void testGetCategorieById_Found() {
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie1));

        Categorie result = categorieService.getCategorieById(1L);

        assertNotNull(result);
        assertEquals("Electronique", result.getNom());
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getCategorieById - Lève une exception si non trouvée")
    void testGetCategorieById_NotFound() {
        when(categorieRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categorieService.getCategorieById(99L));

        assertTrue(exception.getMessage().contains("Catégorie non trouvée"));
    }


    @Test
    @DisplayName("saveCategorie - Sauvegarde réussie d'une nouvelle catégorie")
    void testSaveCategorie_Success() {
        Categorie newCategorie = new Categorie(null, null, "Livres");
        when(categorieRepository.existsByNom("Livres")).thenReturn(false);
        when(categorieRepository.save(any(Categorie.class))).thenReturn(new Categorie(3L, null, "Livres"));

        Categorie result = categorieService.saveCategorie(newCategorie);

        assertNotNull(result);
        assertEquals("Livres", result.getNom());
        assertEquals(3L, result.getId());
        verify(categorieRepository).save(newCategorie);
    }

    @Test
    @DisplayName("saveCategorie - Échec si le nom est vide")
    void testSaveCategorie_EmptyName() {
        Categorie emptyNameCategorie = new Categorie(null, null, "");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categorieService.saveCategorie(emptyNameCategorie));

        assertEquals("Le nom de la catégorie ne peut pas être vide", exception.getMessage());
        verify(categorieRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveCategorie - Échec si le nom est null")
    void testSaveCategorie_NullName() {
        Categorie nullNameCategorie = new Categorie(null, null, null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categorieService.saveCategorie(nullNameCategorie));

        assertEquals("Le nom de la catégorie ne peut pas être vide", exception.getMessage());
    }

    @Test
    @DisplayName("saveCategorie - Échec si une catégorie avec le même nom existe")
    void testSaveCategorie_DuplicateName() {
        Categorie duplicate = new Categorie(null, null, "Electronique");
        when(categorieRepository.existsByNom("Electronique")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categorieService.saveCategorie(duplicate));

        assertEquals("Une catégorie avec ce nom existe déjà", exception.getMessage());
        verify(categorieRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveCategorie - Modification d'une catégorie existante (pas de vérification doublon)")
    void testSaveCategorie_Update() {
        Categorie updated = new Categorie(1L, null, "Electronique Modifiée");
        when(categorieRepository.save(updated)).thenReturn(updated);

        Categorie result = categorieService.saveCategorie(updated);

        assertNotNull(result);
        assertEquals("Electronique Modifiée", result.getNom());
        verify(categorieRepository, never()).existsByNom(anyString());
        verify(categorieRepository).save(updated);
    }


    @Test
    @DisplayName("deleteCategorie - Suppression réussie")
    void testDeleteCategorie_Success() {
        when(categorieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categorieRepository).deleteById(1L);

        assertDoesNotThrow(() -> categorieService.deleteCategorie(1L));

        verify(categorieRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteCategorie - Échec si la catégorie n'existe pas")
    void testDeleteCategorie_NotFound() {
        when(categorieRepository.existsById(99L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> categorieService.deleteCategorie(99L));

        assertTrue(exception.getMessage().contains("Catégorie non trouvée"));
        verify(categorieRepository, never()).deleteById(any());
    }


    @Test
    @DisplayName("categorieExists - Retourne true si la catégorie existe")
    void testCategorieExists_True() {
        when(categorieRepository.existsByNom("Electronique")).thenReturn(true);

        assertTrue(categorieService.categorieExists("Electronique"));
    }

    @Test
    @DisplayName("categorieExists - Retourne false si la catégorie n'existe pas")
    void testCategorieExists_False() {
        when(categorieRepository.existsByNom("Inconnue")).thenReturn(false);

        assertFalse(categorieService.categorieExists("Inconnue"));
    }
}
