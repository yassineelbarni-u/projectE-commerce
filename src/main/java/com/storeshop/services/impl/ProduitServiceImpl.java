package com.storeshop.services.impl;

import com.storeshop.entities.Produit;
import com.storeshop.repositories.ProduitRepository;
import com.storeshop.services.ProduitService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/** {@link ProduitService} implementation delegating queries and persistence to the repository. */
@Service
@Transactional
@AllArgsConstructor
public class ProduitServiceImpl implements ProduitService {

  private final ProduitRepository produitRepository;

  /**
   * Runs admin product search.
   *
   * @param search search text
   * @param page zero-based page index
   * @param size page size
   * @return paginated products
   */
  @Override
  public Page<Produit> searchProduits(String search, int page, int size) {
    return produitRepository.searchProduits(search, PageRequest.of(page, size));
  }

  /**
   * Runs storefront search with optional category filter.
   *
   * @param search search text (null is normalized to empty)
   * @param categorieId optional category id
   * @param page zero-based page index
   * @param size page size
   * @return paginated products
   */
  @Override
  public Page<Produit> searchProduitsPublic(String search, Long categorieId, int page, int size) {
    String normalizedSearch = search == null ? "" : search.trim();
    return produitRepository.searchProduitsPublic(
        normalizedSearch, categorieId, PageRequest.of(page, size));
  }

  /**
   * Loads one product.
   *
   * @param id product id
   * @return product
   * @throws RuntimeException when not found
   */
  @Override
  public Produit getProduitById(Long id) {
    return produitRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Produit non trouve avec id: " + id));
  }

  /**
   * Validates and saves a product.
   *
   * @param produit product to persist
   * @return saved product
   * @throws RuntimeException when name is blank, price is negative, or stock is negative
   */
  @Override
  public Produit saveProduit(Produit produit) {
    if (produit.getName() == null || produit.getName().trim().isEmpty()) {
      throw new RuntimeException("Le nom du produit ne peut pas etre vide");
    }

    if (produit.getPrice() < 0) {
      throw new RuntimeException("Le prix ne peut pas être negatif");
    }

    if (produit.getStock() < 0) {
      throw new RuntimeException("Le stock ne peut pas etre negatif");
    }

    return produitRepository.save(produit);
  }

  /**
   * Deletes one product.
   *
   * @param id product id
   * @throws RuntimeException when id does not exist
   */
  @Override
  public void deleteProduit(Long id) {
    if (!produitRepository.existsById(id)) {
      throw new RuntimeException("Produit non trouve avec l'ID: " + id);
    }
    produitRepository.deleteById(id);
  }

  /**
   * Checks whether a product exists.
   *
   * @param id product id
   * @return true when found
   */
  @Override
  public boolean produitExists(Long id) {
    return produitRepository.existsById(id);
  }
}
