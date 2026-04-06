package com.storeshop.services.impl;

import com.storeshop.entities.Commande;
import com.storeshop.entities.CommandeItem;
import com.storeshop.entities.Produit;
import com.storeshop.entities.User;
import com.storeshop.repositories.CommandeRepository;
import com.storeshop.services.CommandeService;
import com.storeshop.services.ProduitService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Order creation ties line items, stock mutation, and persistence in one transaction so a failure
 * rolls back inventory changes together with the order.
 */
@Service
@Transactional
@AllArgsConstructor
public class CommandeServiceImpl implements CommandeService {

  private final CommandeRepository commandeRepository;
  private final ProduitService produitService;

  /**
   * Creates and saves an order from cart entries.
   *
   * <p>Processing sequence:
   *
   * <p>1) validate cart is not empty,
   *
   * <p>2) validate stock per product,
   *
   * <p>3) decrement stock,
   *
   * <p>4) snapshot unit prices and totals in order lines,
   *
   * <p>5) persist order with computed grand total.
   *
   * @param user customer placing the order
   * @param items productId -> quantity map from cart
   * @return persisted order
   * @throws RuntimeException when cart is empty or stock is insufficient
   */
  @Override
  public Commande createOrder(User user, Map<Long, Integer> items) {
    if (items == null || items.isEmpty()) {
      throw new RuntimeException("Le panier est vide");
    }

    Commande commande = new Commande();
    commande.setUser(user);
    commande.setCreatedAt(LocalDateTime.now());
    commande.setStatus("VALIDEE");

    double total = 0;

    for (Map.Entry<Long, Integer> entry : items.entrySet()) {
      Long produitId = entry.getKey();
      int quantity = entry.getValue();

      Produit produit = produitService.getProduitById(produitId);
      if (quantity > produit.getStock()) {
        throw new RuntimeException("Stock insuffisant pour: " + produit.getName());
      }

      produit.setStock(produit.getStock() - quantity);
      produitService.saveProduit(produit);

      CommandeItem item = new CommandeItem();
      item.setProduit(produit);
      item.setQuantity(quantity);
      item.setUnitPrice(produit.getPrice());
      item.setLineTotal(produit.getPrice() * quantity);
      commande.addItem(item);
      total += item.getLineTotal();
    }

    commande.setTotal(total);
    return commandeRepository.save(commande);
  }

  /**
   * Returns one user's orders sorted newest first.
   *
   * @param user customer
   * @return list of orders
   */
  @Override
  public List<Commande> listUserOrders(User user) {
    return commandeRepository.findByUserOrderByCreatedAtDesc(user);
  }

  /**
   * Returns all orders sorted newest first.
   *
   * @return list of orders
   */
  @Override
  public List<Commande> listAllOrders() {
    return commandeRepository.findAllByOrderByCreatedAtDesc();
  }

  /**
   * Updates status of an existing order.
   *
   * @param commandeId order id
   * @param status new status value
   * @throws RuntimeException when order does not exist
   */
  @Override
  public void updateStatus(Long commandeId, String status) {
    Commande commande =
        commandeRepository
            .findById(commandeId)
            .orElseThrow(() -> new RuntimeException("Commande introuvable"));
    commande.setStatus(status);
    commandeRepository.save(commande);
  }
}
