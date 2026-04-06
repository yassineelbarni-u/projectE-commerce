package com.storeshop.controllers;

import com.storeshop.entities.Categorie;
import com.storeshop.services.CategorieService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/categories")
@AllArgsConstructor
/**
 * Admin controller for category management pages.
 */
public class CategorieController {

  private final CategorieService categorieService;

  /**
   * Displays all categories.
   *
   * @param model MVC model receiving categories list
   * @return categories list template
   */
  @GetMapping
  public String index(Model model) {
    List<Categorie> categories = categorieService.getAllCategories();
    model.addAttribute("listeCategories", categories);
    return "categorie/ListeCategorie";
  }

  /**
   * Opens the add-category form.
   *
   * @param model MVC model containing an empty category object
   * @return add-category template
   */
  @GetMapping("/add")
  public String showAddForm(Model model) {
    model.addAttribute("categorie", new Categorie());
    return "categorie/ajouterCategorie";
  }

  /**
   * Creates a category from form values.
   *
   * @param categorie submitted category object
   * @return redirect to list page on success or back to add form with error message
   */
  @PostMapping("/add")
  public String addCategorie(@ModelAttribute Categorie categorie) {
    try {
      categorieService.saveCategorie(categorie);
      return "redirect:/admin/categories";
    } catch (RuntimeException e) {

      return "redirect:/admin/categories/add?error=" + e.getMessage();
    }
  }

  /**
   * Opens the edit form for one category.
   *
   * @param id category id
   * @param model MVC model receiving the selected category
   * @return edit-category template
   */
  @GetMapping("/edit")
  public String showEditForm(@RequestParam(name = "id") Long id, Model model) {
    Categorie categorie = categorieService.getCategorieById(id);
    model.addAttribute("categorie", categorie);
    return "categorie/editCategorie";
  }

  /**
   * Applies category changes.
   *
   * @param categorie submitted category object
   * @return redirect to list page on success or back to edit page with error message
   */
  @PostMapping("/edit")
  public String editCategorie(@ModelAttribute Categorie categorie) {
    try {
      categorieService.saveCategorie(categorie);
      return "redirect:/admin/categories";
    } catch (RuntimeException e) {
      return "redirect:/admin/categories/edit?id=" + categorie.getId() + "&error=" + e.getMessage();
    }
  }

  /**
   * Deletes one category.
   *
   * @param id category id
   * @return redirect to list page with success/error flag
   */
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
