package com.storeshop.repositories;

import com.storeshop.entities.Commande;
import com.storeshop.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

  List<Commande> findByUserOrderByCreatedAtDesc(User user);

  List<Commande> findAllByOrderByCreatedAtDesc();
}
