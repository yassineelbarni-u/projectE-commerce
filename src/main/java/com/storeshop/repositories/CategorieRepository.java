package com.storeshop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.storeshop.entities.Categorie;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    
    boolean existsByNom(String nom);
    
    Categorie findByNom(String nom);
}