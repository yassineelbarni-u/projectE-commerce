package com.storeshop.services;

import java.util.List;

import com.storeshop.entities.Categorie;

public interface CategorieService {

  List<Categorie> getAllCategories();
  Categorie getCategorieById(Long id);
  Categorie saveCategorie(Categorie categorie);
  void deleteCategorie(Long id);
  boolean categorieExists(String nom);
}
