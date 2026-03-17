package com.storeshop.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.storeshop.entities.Produit;
import com.storeshop.repositories.ProduitRepository;
import com.storeshop.services.ProduitService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    

    @Override
    public Page<Produit> searchProduits(String search, int page, int size) {
        // Utiliser la méthode de recherche du repository
        return produitRepository.searchProduits(search, PageRequest.of(page, size));
    }

    @Override
    public Page<Produit> searchProduitsPublic(String search, Long categorieId, int page, int size) {
        String normalizedSearch = search == null ? "" : search.trim();
        return produitRepository.searchProduitsPublic(normalizedSearch, categorieId, PageRequest.of(page, size));
    }

    @Override
    public Produit getProduitById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouve avec id: " + id));
    }

    @Override
    public Produit saveProduit(Produit produit) {
        // Validation de base
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

    @Override
    public void deleteProduit(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouve avec l'ID: " + id);
        }
        produitRepository.deleteById(id);
    }

    @Override
    public boolean produitExists(Long id) {
        return produitRepository.existsById(id);
    }
}
