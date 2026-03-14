package fstm.ilisi.Gestion_bibliotheque.service;

import org.springframework.data.domain.Page;

import fstm.ilisi.Gestion_bibliotheque.entity.Produit;

public interface ProduitService {

    Page<Produit> searchProduits(String search, int page, int size);

    Page<Produit> searchProduitsPublic(String search, Long categorieId, int page, int size);
    
    Produit getProduitById(Long id);
    
    Produit saveProduit(Produit produit);
    
    void deleteProduit(Long id);
    
    boolean produitExists(Long id);
}
