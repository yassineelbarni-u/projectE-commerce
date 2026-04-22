package com.storeshop.unit.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.storeshop.entities.Commande;
import com.storeshop.entities.CommandeItem;
import com.storeshop.entities.Produit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests de l'entité Commande")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class CommandeTest {

  @Test
  @DisplayName("addItem maintient la relation bidirectionnelle avec CommandeItem")
  void addItemSetsBackReference() {
    Commande commande = new Commande();
    Produit produit = new Produit();
    produit.setName("Clavier");

    CommandeItem item = new CommandeItem();
    item.setProduit(produit);
    item.setQuantity(2);

    commande.addItem(item);

    assertEquals(1, commande.getItems().size());
    assertSame(item, commande.getItems().get(0));
    assertSame(commande, item.getCommande());
  }
}
