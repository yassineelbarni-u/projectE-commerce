package com.storeshop.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persisted order aggregate.
 *
 * <p>An order belongs to one user and owns multiple {@link CommandeItem} rows. The order total is
 * stored as a snapshot value computed during checkout.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Commande {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Customer that placed this order. */
  @ManyToOne(optional = false)
  private User user;

  /** Date-time when checkout was completed. */
  private LocalDateTime createdAt;

  /** Workflow status, for example VALIDEE, EXPEDIEE, ANNULEE. */
  private String status;

  /** Grand total for all order lines. */
  private double total;

  /**
   * Order lines linked to this order.
   *
   * <p>{@code cascade = ALL} + {@code orphanRemoval = true} means line lifecycle is owned by the
   * order entity.
   */
  @OneToMany(
      mappedBy = "commande",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<CommandeItem> items = new ArrayList<>();

  /**
   * Adds one line item and synchronizes both sides of the JPA relationship.
   *
   * @param item line item to attach to this order
   */
  public void addItem(CommandeItem item) {
    items.add(item);
    item.setCommande(this);
  }
}
