package com.storeshop.repositories;

import com.storeshop.entities.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
/**
 * Repository for category persistence and existence checks.
 */
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

  /**
   * Checks if a category with the given name exists.
   *
   * @param nom category name
   * @return true when at least one category uses this name
   */
  boolean existsByNom(String nom);

  /**
   * Finds category by exact name.
   *
   * @param nom category name
   * @return matching category or null
   */
  Categorie findByNom(String nom);
}
