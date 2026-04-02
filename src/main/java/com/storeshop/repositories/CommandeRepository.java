package com.storeshop.repositories;

import com.storeshop.entities.Commande;
import com.storeshop.entities.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

  /*~~(class org.openrewrite.java.tree.J$Erroneous cannot be cast to class org.openrewrite.java.tree.J$Assignment (org.openrewrite.java.tree.J$Erroneous and org.openrewrite.java.tree.J$Assignment are in unnamed module of loader 'app'))~~>*/@Query("SELECT c FROM Commande c WHERE c.user = :user ORDER BY c.createdAt desc")
  List<Commande> findByUserOrderByCreatedAtDesc(User user);

  List<Commande> findAllByOrderByCreatedAtDesc();
}

