package com.storeshop.services.impl;

import com.storeshop.entities.Categorie;
import com.storeshop.repositories.CategorieRepository;
import com.storeshop.services.CategorieService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/** {@link CategorieService} backed by {@link CategorieRepository} with name rules on create. */
@Service
@Transactional
@AllArgsConstructor
public class CategorieServiceImpl implements CategorieService {

  private final CategorieRepository categorieRepository;

  /**
   * Returns all categories.
   *
   * @return category list
   */
  @Override
  public List<Categorie> getAllCategories() {
    return categorieRepository.findAll();
  }

  /**
   * Loads one category by id.
   *
   * @param id category id
   * @return category
   * @throws RuntimeException when id is unknown
   */
  @Override
  public Categorie getCategorieById(Long id) {
    return categorieRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + id));
  }

  /**
   * Validates and saves a category.
   *
   * <p>On creation only (id == null), category name must be unique.
   *
   * @param categorie category to create or update
   * @return persisted category
   * @throws RuntimeException when name is blank or duplicated for a new row
   */
  @Override
  public Categorie saveCategorie(Categorie categorie) {
    if (categorie.getNom() == null || categorie.getNom().trim().isEmpty()) {
      throw new RuntimeException("Le nom de la catégorie ne peut pas être vide");
    }

    // Duplicate name guard applies only to inserts; updates keep their id and skip this branch.
    if (categorie.getId() == null) {
      if (categorieRepository.existsByNom(categorie.getNom())) {
        throw new RuntimeException("Une catégorie avec ce nom existe déjà");
      }
    }

    return categorieRepository.save(categorie);
  }

  /**
   * Deletes a category by id.
   *
   * @param id category id
   * @throws RuntimeException when id does not exist
   */
  @Override
  public void deleteCategorie(Long id) {
    if (!categorieRepository.existsById(id)) {
      throw new RuntimeException("Catégorie non trouvée avec l'ID: " + id);
    }
    categorieRepository.deleteById(id);
  }

  /**
   * Checks category existence by name.
   *
   * @param nom category name
   * @return true if category exists
   */
  @Override
  public boolean categorieExists(String nom) {
    return categorieRepository.existsByNom(nom);
  }
}
