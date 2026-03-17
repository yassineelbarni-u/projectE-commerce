package com.storeshop.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.storeshop.entities.Categorie;
import com.storeshop.repositories.CategorieRepository;
import com.storeshop.services.CategorieService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class CategorieServiceImpl implements CategorieService {

    private final CategorieRepository categorieRepository;

    @Override
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public Categorie getCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + id));
    }

    @Override
    public Categorie saveCategorie(Categorie categorie) {
        // Validation: vérifier que le nom n'est pas vide
        if (categorie.getNom() == null || categorie.getNom().trim().isEmpty()) {
            throw new RuntimeException("Le nom de la catégorie ne peut pas être vide");
        }
        
        // Vérifier si une catégorie avec le même nom existe déjà (sauf si c'est une modification)
        if (categorie.getId() == null) {
            if (categorieRepository.existsByNom(categorie.getNom())) {
                throw new RuntimeException("Une catégorie avec ce nom existe déjà");
            }
        }
        
        return categorieRepository.save(categorie);
    }

    @Override
    public void deleteCategorie(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new RuntimeException("Catégorie non trouvée avec l'ID: " + id);
        }
        categorieRepository.deleteById(id);
    }

    @Override
    public boolean categorieExists(String nom) {
        return categorieRepository.existsByNom(nom);
    }
}
