package com.storeshop.controllers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.storeshop.entities.Categorie;
import com.storeshop.services.CategorieService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller CategorieController")
class CategorieControllerTest {

    @Mock
    private CategorieService categorieService;

    @Mock
    private Model model;

    @InjectMocks
    private CategorieController categorieController;

    private MockMvc mockMvc;
    private Categorie categorie1;
    private Categorie categorie2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categorieController).build();
        categorie1 = new Categorie(1L, null, "Electronique");
        categorie2 = new Categorie(2L, null, "Informatique");
    }


    @Test
    @DisplayName("Test index - Liste des catégories")
    void testIndex() {
        List<Categorie> categories = Arrays.asList(categorie1, categorie2);
        when(categorieService.getAllCategories()).thenReturn(categories);

        String view = categorieController.index(model);

        assertEquals("categorie/ListeCategorie", view);
        verify(model).addAttribute("listeCategories", categories);
        verify(categorieService).getAllCategories();
    }

    @Test
    @DisplayName("Test index avec MockMvc")
    void testIndexWithMockMvc() throws Exception {
        when(categorieService.getAllCategories()).thenReturn(Arrays.asList(categorie1, categorie2));

        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorie/ListeCategorie"));
    }


    @Test
    @DisplayName("Test showAddForm - Affiche le formulaire d'ajout")
    void testShowAddForm() {
        String view = categorieController.showAddForm(model);

        assertEquals("categorie/ajouterCategorie", view);
        verify(model).addAttribute(eq("categorie"), any(Categorie.class));
    }

    @Test
    @DisplayName("Test showAddForm avec MockMvc")
    void testShowAddFormWithMockMvc() throws Exception {
        mockMvc.perform(get("/admin/categories/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorie/ajouterCategorie"));
    }


    @Test
    @DisplayName("Test addCategorie - Ajout réussi")
    void testAddCategorie_Success() {
        Categorie newCategorie = new Categorie(null, null, "Livres");
        when(categorieService.saveCategorie(newCategorie)).thenReturn(new Categorie(3L, null, "Livres"));

        String redirect = categorieController.addCategorie(newCategorie);

        assertEquals("redirect:/admin/categories", redirect);
        verify(categorieService).saveCategorie(newCategorie);
    }

    @Test
    @DisplayName("Test addCategorie - Échec avec erreur")
    void testAddCategorie_Error() {
        Categorie duplicate = new Categorie(null, null, "Electronique");
        when(categorieService.saveCategorie(duplicate))
                .thenThrow(new RuntimeException("Une catégorie avec ce nom existe déjà"));

        String redirect = categorieController.addCategorie(duplicate);

        assertTrue(redirect.startsWith("redirect:/admin/categories/add?error="));
    }


    @Test
    @DisplayName("Test showEditForm - Affiche le formulaire d'édition")
    void testShowEditForm() {
        when(categorieService.getCategorieById(1L)).thenReturn(categorie1);

        String view = categorieController.showEditForm(1L, model);

        assertEquals("categorie/editCategorie", view);
        verify(model).addAttribute("categorie", categorie1);
    }

    @Test
    @DisplayName("Test showEditForm avec MockMvc")
    void testShowEditFormWithMockMvc() throws Exception {
        when(categorieService.getCategorieById(1L)).thenReturn(categorie1);

        mockMvc.perform(get("/admin/categories/edit").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("categorie/editCategorie"));
    }


    @Test
    @DisplayName("Test editCategorie - Modification réussie")
    void testEditCategorie_Success() {
        Categorie updated = new Categorie(1L, null, "Electronique Modifiée");
        when(categorieService.saveCategorie(updated)).thenReturn(updated);

        String redirect = categorieController.editCategorie(updated);

        assertEquals("redirect:/admin/categories", redirect);
        verify(categorieService).saveCategorie(updated);
    }

    @Test
    @DisplayName("Test editCategorie - Echec avec erreur")
    void testEditCategorie_Error() {
        Categorie invalid = new Categorie(1L, null, "");
        when(categorieService.saveCategorie(invalid))
                .thenThrow(new RuntimeException("Le nom de la catégorie ne peut pas être vide"));

        String redirect = categorieController.editCategorie(invalid);

        assertTrue(redirect.contains("redirect:/admin/categories/edit?id=1"));
    }

    // ====== Tests deleteCategorie ========

    @Test
    @DisplayName("Test deleteCategorie - Suppression réussie")
    void testDeleteCategorie_Success() {
        doNothing().when(categorieService).deleteCategorie(1L);

        String redirect = categorieController.deleteCategorie(1L);

        assertEquals("redirect:/admin/categories", redirect);
        verify(categorieService).deleteCategorie(1L);
    }

    @Test
    @DisplayName("Test deleteCategorie - Échec avec erreur")
    void testDeleteCategorie_Error() {
        doThrow(new RuntimeException("Catégorie non trouvée")).when(categorieService).deleteCategorie(99L);

        String redirect = categorieController.deleteCategorie(99L);

        assertTrue(redirect.contains("redirect:/admin/categories?error="));
    }

    @Test
    @DisplayName("Test deleteCategorie avec MockMvc")
    void testDeleteCategorieWithMockMvc() throws Exception {
        doNothing().when(categorieService).deleteCategorie(1L);

        mockMvc.perform(get("/admin/categories/delete").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));
    }
}
