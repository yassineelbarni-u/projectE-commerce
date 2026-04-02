package com.storeshop.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.storeshop.entities.*;
import com.storeshop.repositories.CommandeRepository;
import com.storeshop.services.impl.CommandeServiceImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service CommandeServiceImpl")
class CommandeServiceImplTest {

  @Mock private CommandeRepository commandeRepository;
  @Mock private ProduitService produitService;

  @InjectMocks private CommandeServiceImpl commandeService;

  private User user;
  private Produit produit1;
  private Produit produit2;

  @BeforeEach
  void setUp() {
    user = User.builder()
        .userId("uuid-1")
        .username("client1")
        .email("client1@gmail.com")
        .role(Role.CLIENT)
        .build();

    produit1 = new Produit();
    produit1.setId(1L);
    produit1.setName("Smartphone");
    produit1.setPrice(800.0);
    produit1.setStock(10);

    produit2 = new Produit();
    produit2.setId(2L);
    produit2.setName("Laptop");
    produit2.setPrice(1200.0);
    produit2.setStock(5);
  }

  @Test
  @DisplayName("createOrder - crée une commande avec succès")
  void testCreateOrder_Success() {
    Map<Long, Integer> items = new HashMap<>();
    items.put(1L, 2);
    items.put(2L, 1);

    when(produitService.getProduitById(1L)).thenReturn(produit1);
    when(produitService.getProduitById(2L)).thenReturn(produit2);
    when(produitService.saveProduit(any(Produit.class))).thenAnswer(i -> i.getArgument(0));
    when(commandeRepository.save(any(Commande.class))).thenAnswer(i -> i.getArgument(0));

    Commande result = commandeService.createOrder(user, items);

    assertNotNull(result);
    assertEquals(CommandeStatus.VALIDEE, result.getStatus());
    assertEquals(user, result.getUser());
    assertEquals(2800.0, result.getTotal(), 0.01);
    verify(commandeRepository).save(any(Commande.class));
  }

  @Test
  @DisplayName("createOrder - lève une exception si panier vide")
  void testCreateOrder_EmptyCart() {
    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> commandeService.createOrder(user, new HashMap<>()));
    assertEquals("Le panier est vide", ex.getMessage());
    verify(commandeRepository, never()).save(any());
  }

  @Test
  @DisplayName("createOrder - lève une exception si panier null")
  void testCreateOrder_NullCart() {
    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> commandeService.createOrder(user, null));
    assertEquals("Le panier est vide", ex.getMessage());
  }

  @Test
  @DisplayName("createOrder - lève une exception si stock insuffisant")
  void testCreateOrder_InsufficientStock() {
    Map<Long, Integer> items = new HashMap<>();
    items.put(1L, 20);

    when(produitService.getProduitById(1L)).thenReturn(produit1);

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> commandeService.createOrder(user, items));
    assertTrue(ex.getMessage().contains("Stock insuffisant"));
  }

  @Test
  @DisplayName("createOrder - décrémente le stock après commande")
  void testCreateOrder_DecrementsStock() {
    Map<Long, Integer> items = new HashMap<>();
    items.put(1L, 3);

    when(produitService.getProduitById(1L)).thenReturn(produit1);
    when(produitService.saveProduit(any(Produit.class))).thenAnswer(i -> i.getArgument(0));
    when(commandeRepository.save(any(Commande.class))).thenAnswer(i -> i.getArgument(0));

    commandeService.createOrder(user, items);

    assertEquals(7, produit1.getStock());
  }

  @Test
  @DisplayName("listUserOrders - retourne les commandes de l'utilisateur")
  void testListUserOrders() {
    Commande c1 = new Commande();
    c1.setUser(user);
    Commande c2 = new Commande();
    c2.setUser(user);

    when(commandeRepository.findByUserOrderByCreatedAtDesc(user))
        .thenReturn(List.of(c1, c2));

    List<Commande> result = commandeService.listUserOrders(user);

    assertEquals(2, result.size());
    verify(commandeRepository).findByUserOrderByCreatedAtDesc(user);
  }

  @Test
  @DisplayName("listAllOrders - retourne toutes les commandes")
  void testListAllOrders() {
    Commande c1 = new Commande();
    Commande c2 = new Commande();
    Commande c3 = new Commande();

    when(commandeRepository.findAllByOrderByCreatedAtDesc())
        .thenReturn(List.of(c1, c2, c3));

    List<Commande> result = commandeService.listAllOrders();

    assertEquals(3, result.size());
    verify(commandeRepository).findAllByOrderByCreatedAtDesc();
  }

  @Test
  @DisplayName("updateStatus - met à jour le statut d'une commande")
  void testUpdateStatus_Success() {
    Commande commande = new Commande();
    commande.setStatus(CommandeStatus.VALIDEE);

    when(commandeRepository.findById(1L)).thenReturn(Optional.of(commande));
    when(commandeRepository.save(any(Commande.class))).thenAnswer(i -> i.getArgument(0));

    commandeService.updateStatus(1L, "LIVREE");

    assertEquals(CommandeStatus.LIVREE, commande.getStatus());
    verify(commandeRepository).save(commande);
  }

  @Test
  @DisplayName("updateStatus - lève une exception si commande introuvable")
  void testUpdateStatus_NotFound() {
    when(commandeRepository.findById(99L)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> commandeService.updateStatus(99L, "LIVREE"));
    assertEquals("Commande introuvable", ex.getMessage());
    verify(commandeRepository, never()).save(any());
  }
}