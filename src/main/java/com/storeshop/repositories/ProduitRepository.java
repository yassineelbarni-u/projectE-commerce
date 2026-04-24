package com.storeshop.repositories;

import com.storeshop.entities.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for product persistence and custom search queries.
 */
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    /**
     * Finds a product by exact name.
     *
     * @param name exact product name
     * @return matching product or null when not found
     */
    Produit findByName(String name);

    /**
     * Admin search query (case-insensitive) on name, description, and category name.
     *
     * @param search text filter
     * @param pageable page request (page index, size, sort)
     * @return paginated matching products
     */
  @Query(
      "SELECT p FROM Produit p WHERE "
          + "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
          + "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) OR "
          + "LOWER(p.categorie.nom) LIKE LOWER(CONCAT('%', :search, '%')) "
          + "ORDER BY p.id DESC")
  Page<Produit> searchProduits(@Param("search") String search, Pageable pageable);

    /**
     * Public storefront search with optional category filter.
     *
     * @param search text filter; empty string disables text filtering
     * @param categorieId optional category id; null means all categories
     * @param pageable page request
     * @return paginated matching products
     */
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
