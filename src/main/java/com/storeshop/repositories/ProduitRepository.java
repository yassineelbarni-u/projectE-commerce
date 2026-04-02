package com.storeshop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.storeshop.entities.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

  // Search in name, description and category name
  // Java Persistence Query Language (JPQL) query to search products by name, description, or category name
  @Query(
      "SELECT p FROM Produit p WHERE "
          + "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
          + "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
          + "LOWER(p.categorie.nom) LIKE LOWER(CONCAT('%', :search, '%'))")
  Page<Produit> searchProduits(@Param("search") String search, Pageable pageable);

  @Query(
      "SELECT p FROM Produit p LEFT JOIN p.categorie c WHERE "
          + "(:categorieId IS NULL OR c.id = :categorieId) AND ("
          + ":search = '' OR "
          + "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
          + "LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR "
          + "LOWER(COALESCE(c.nom, '')) LIKE LOWER(CONCAT('%', :search, '%')))")
  Page<Produit> searchProduitsPublic(
      @Param("search") String search, @Param("categorieId") Long categorieId, Pageable pageable);
}
