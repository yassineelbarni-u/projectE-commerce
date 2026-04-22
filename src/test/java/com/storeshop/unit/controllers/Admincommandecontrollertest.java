package com.storeshop.unit.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.storeshop.controllers.AdminCommandeController;
import com.storeshop.entities.Commande;
import com.storeshop.services.CommandeService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

/**
 * Tests unitaires pour {@link AdminCommandeController}.
 * Vérifie la gestion des commandes par l'administrateur, notamment l'affichage et la mise à jour des statuts.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller AdminCommandeController")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class AdminCommandeControllerTest {

  @Mock private CommandeService commandeService;
  @Mock private Model model;

  @InjectMocks private AdminCommandeController adminCommandeController;

  private MockMvc mockMvc;
  private Commande commande1;
  private Commande commande2;

  /**
   * Initialisation des mocks et des données de test avant chaque test.
   */
  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(adminCommandeController).build();

    commande1 = new Commande();
    commande1.setStatus("VALIDEE");

    commande2 = new Commande();
    commande2.setStatus("LIVREE");
  }

  /**
   * Teste l'affichage de la liste des commandes.
   */
  @Test
  @DisplayName("listOrders - retourne la vue admin/commandes avec la liste")
  void testListOrders() {
    when(commandeService.listAllOrders()).thenReturn(List.of(commande1, commande2));

    String view = adminCommandeController.listOrders(model);

    assertEquals("admin/commandes", view);
    verify(model).addAttribute("orders", List.of(commande1, commande2));
    verify(commandeService).listAllOrders();
  }

  /**
   * Teste l'affichage de la liste avec MockMvc.
   * 
   * @throws Exception En cas d'erreur MockMvc.
   */
  @Test
  @DisplayName("listOrders - avec MockMvc retourne status 200")
  void testListOrders_MockMvc() throws Exception {
    when(commandeService.listAllOrders()).thenReturn(List.of(commande1, commande2));

    mockMvc
        .perform(get("/admin/commandes"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin/commandes"));
  }

  /**
   * Teste le cas où aucune commande n'est présente.
   */
  @Test
  @DisplayName("listOrders - liste vide retourne quand même la vue")
  void testListOrders_EmptyList() {
    when(commandeService.listAllOrders()).thenReturn(List.of());

    String view = adminCommandeController.listOrders(model);

    assertEquals("admin/commandes", view);
    verify(model).addAttribute("orders", List.of());
  }

  /**
   * Teste la mise à jour du statut d'une commande.
   */
  @Test
  @DisplayName("updateStatus - met à jour le statut et redirige")
  void testUpdateStatus_Success() {
    doNothing().when(commandeService).updateStatus(1L, "LIVREE");

    String redirect = adminCommandeController.updateStatus(1L, "LIVREE");

    assertEquals("redirect:/admin/commandes?success", redirect);
    verify(commandeService).updateStatus(1L, "LIVREE");
  }

  /**
   * Teste la mise à jour du statut via MockMvc.
   * 
   * @throws Exception En cas d'erreur MockMvc.
   */
  @Test
  @DisplayName("updateStatus - avec MockMvc redirige après POST")
  void testUpdateStatus_MockMvc() throws Exception {
    doNothing().when(commandeService).updateStatus(1L, "LIVREE");

    mockMvc
        .perform(post("/admin/commandes/status").param("id", "1").param("status", "LIVREE"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/commandes?success"));
  }


  /**
   * Teste la mise à jour avec différents statuts possibles.
   */
  @Test
  @DisplayName("updateStatus - différents statuts possibles")
  void testUpdateStatus_DifferentStatuses() {
    doNothing().when(commandeService).updateStatus(anyLong(), anyString());

    assertEquals(
        "redirect:/admin/commandes?success", adminCommandeController.updateStatus(1L, "EN_COURS"));
    assertEquals(
        "redirect:/admin/commandes?success", adminCommandeController.updateStatus(2L, "ANNULEE"));
    assertEquals(
        "redirect:/admin/commandes?success", adminCommandeController.updateStatus(3L, "VALIDEE"));
  }
}
