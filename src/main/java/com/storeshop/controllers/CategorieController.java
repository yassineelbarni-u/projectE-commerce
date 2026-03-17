package com.storeshop.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.storeshop.entities.Categorie;
import com.storeshop.services.CategorieService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/categories")
@AllArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    // Afficher la liste des catégories
    @GetMapping
    public String index(Model model) {
        List<Categorie> categories = categorieService.getAllCategories();
        model.addAttribute("listeCategories", categories);
        return "categorie/ListeCategorie";
    }

    // Afficher le formulaire d'ajout
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("categorie", new Categorie());
        return "categorie/ajouterCategorie";
    }

    // Sauvegarder une nouvelle catégorie
    @PostMapping("/add")
    public String addCategorie(@ModelAttribute Categorie categorie) {
        try {
            categorieService.saveCategorie(categorie);
            return "redirect:/admin/categories";
        } catch (RuntimeException e) {
            
            // En cas d'erreur (nom déjà existant, etc.)
            return "redirect:/admin/categories/add?error=" + e.getMessage();
        }
    }

    // Afficher le formulaire d'édition
    @GetMapping("/edit")
    public String showEditForm(@RequestParam(name = "id") Long id, Model model) {
        Categorie categorie = categorieService.getCategorieById(id);
        model.addAttribute("categorie", categorie);
        return "categorie/editCategorie";
    }

    // Mettre à jour une catégorie
    @PostMapping("/edit")
    public String editCategorie(@ModelAttribute Categorie categorie) {
        try {
            categorieService.saveCategorie(categorie);
            return "redirect:/admin/categories";
        } catch (RuntimeException e) {
            return "redirect:/admin/categories/edit?id=" + categorie.getId() + "&error=" + e.getMessage();
        }
    }

    // Supprimer une catégorie
    @GetMapping("/delete")
    public String deleteCategorie(@RequestParam(name = "id") Long id) {
        try {
            categorieService.deleteCategorie(id);
            return "redirect:/admin/categories";
        } catch (RuntimeException e) {
            return "redirect:/admin/categories?error=" + e.getMessage();
        }
    }
}
