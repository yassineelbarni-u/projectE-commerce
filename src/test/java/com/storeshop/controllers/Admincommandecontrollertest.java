package com.storeshop.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Controller AdminCommandeController")
class AdminCommandeControllerTest {

  @Mock private CommandeService commandeService;
  @Mock private Model model;

  @InjectMocks private AdminCommandeController adminCommandeController;

  private MockMvc mockMvc;
  private Commande commande1;
  private Commande commande2;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(adminCommandeController).build();

    commande1 = new Commande();
    commande1.setStatus("VALIDEE");

    commande2 = new Commande();
    commande2.setStatus("LIVREE");
  }

  @Test
  @DisplayName("listOrders - retourne la vue admin/commandes avec la liste")
  void testListOrders() {
    when(commandeService.listAllOrders()).thenReturn(List.of(commande1, commande2));

    String view = adminCommandeController.listOrders(model);

    assertEquals("admin/commandes", view);
    verify(model).addAttribute("orders", List.of(commande1, commande2));
    verify(commandeService).listAllOrders();
  }

  @Test
  @DisplayName("listOrders - avec MockMvc retourne status 200")
  void testListOrders_MockMvc() throws Exception {
    when(commandeService.listAllOrders()).thenReturn(List.of(commande1, commande2));

    mockMvc.perform(get("/admin/commandes"))
        .andExpect(status().isOk())
        .andExpect(view().name("admin/commandes"));
  }

  @Test
  @DisplayName("listOrders - liste vide retourne quand même la vue")
  void testListOrders_EmptyList() {
    when(commandeService.listAllOrders()).thenReturn(List.of());

    String view = adminCommandeController.listOrders(model);

    assertEquals("admin/commandes", view);
    verify(model).addAttribute("orders", List.of());
  }

  @Test
  @DisplayName("updateStatus - met à jour le statut et redirige")
  void testUpdateStatus_Success() {
    doNothing().when(commandeService).updateStatus(1L, "LIVREE");

    String redirect = adminCommandeController.updateStatus(1L, "LIVREE");

    assertEquals("redirect:/admin/commandes?success", redirect);
    verify(commandeService).updateStatus(1L, "LIVREE");
  }

  @Test
  @DisplayName("updateStatus - avec MockMvc redirige après POST")
  void testUpdateStatus_MockMvc() throws Exception {
    doNothing().when(commandeService).updateStatus(1L, "LIVREE");

    mockMvc.perform(post("/admin/commandes/status")
            .param("id", "1")
            .param("status", "LIVREE"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/admin/commandes?success"));
  }

  @Test
  @DisplayName("updateStatus - différents statuts possibles")
  void testUpdateStatus_DifferentStatuses() {
    doNothing().when(commandeService).updateStatus(anyLong(), anyString());

    assertEquals("redirect:/admin/commandes?success",
        adminCommandeController.updateStatus(1L, "EN_COURS"));
    assertEquals("redirect:/admin/commandes?success",
        adminCommandeController.updateStatus(2L, "ANNULEE"));
    assertEquals("redirect:/admin/commandes?success",
        adminCommandeController.updateStatus(3L, "VALIDEE"));
  }
}