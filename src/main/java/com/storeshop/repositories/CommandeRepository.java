package com.storeshop.repositories;

import com.storeshop.entities.Commande;
import com.storeshop.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

  @Query("SELECT c FROM Commande c WHERE c.user = :user ORDER BY c.createdAt desc")
  List<Commande> findByUserOrderByCreatedAtDesc(User user);

  List<Commande> findAllByOrderByCreatedAtDesc();
}

