package com.storeshop.repositories;

import com.storeshop.entities.Commande;
import com.storeshop.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for order reads/writes and order-history views.
 */
public interface CommandeRepository extends JpaRepository<Commande, Long> {

  /**
   * Returns one user's orders sorted by most recent first.
   *
   * @param user order owner
   * @return descending list by creation time
   */
  List<Commande> findByUserOrderByCreatedAtDesc(User user);

  /**
   * Returns all orders sorted by most recent first.
   *
   * @return descending list by creation time
   */
  List<Commande> findAllByOrderByCreatedAtDesc();
}
