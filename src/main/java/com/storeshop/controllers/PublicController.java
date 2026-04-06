package com.storeshop.controllers;

import com.storeshop.entities.Produit;
import com.storeshop.services.AccountService;
import com.storeshop.services.CategorieService;
import com.storeshop.services.ProduitService;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

@Controller
@AllArgsConstructor
/**
 * Public storefront controller.
 *
 * <p>Handles home/catalog pages, product detail pages, and customer registration.
 */
public class PublicController {

  private final ProduitService produitService;
  private final CategorieService categorieService;
  private final AccountService accountService;

  /**
   * Displays the public catalog with optional text search and category filter.
   *
   * @param model MVC model used by the home template
   * @param page zero-based page index
   * @param size number of products per page
   * @param search free-text query
   * @param categorieId optional category filter; null means all categories
   * @return public home template
   */
  @GetMapping({"/", "/home", "/produits"})
  public String home(
      Model model,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "8") int size,
      @RequestParam(name = "search", defaultValue = "") String search,
      @RequestParam(name = "categorieId", required = false) Long categorieId) {

    Page<Produit> produitsPage =
        produitService.searchProduitsPublic(search, categorieId, page, size);
    model.addAttribute("ListeProduit", produitsPage.getContent());
    model.addAttribute("pages", new int[produitsPage.getTotalPages()]);
    model.addAttribute("currentPage", page);
    model.addAttribute("search", search);
    model.addAttribute("selectedCategorieId", categorieId);
    model.addAttribute("categories", categorieService.getAllCategories());
    return "public/home";
  }

  /**
   * Shows details for one product.
   *
   * @param id product id
   * @param model MVC model receiving the selected product
   * @return product detail template
   */
  @GetMapping("/produits/detail")
  public String showProduitDetail(@RequestParam(name = "id") Long id, Model model) {
    model.addAttribute("produit", produitService.getProduitById(id));
    return "public/detail-produit";
  }

  /**
   * Opens the registration form.
   *
   * @return registration template
   */
  @GetMapping("/register")
  public String showRegisterForm() {
    return "public/register";
  }

  /**
   * Registers a new customer account.
   *
   * @param username desired login name
   * @param email customer email address
   * @param password plain password
   * @param confirmPassword confirmation password
   * @return login redirect on success, or registration redirect carrying encoded error and form
   *     values on failure
   */
  @PostMapping("/register")
  public String register(
      @RequestParam String username,
      @RequestParam String email,
      @RequestParam String password,
      @RequestParam String confirmPassword) {
    try {
      accountService.AddUser(username, password, email, confirmPassword);
      return "redirect:/login?registered";
    } catch (RuntimeException e) {
      return "redirect:/register?error="
          + encode(e.getMessage())
          + "&username="
          + encode(username)
          + "&email="
          + encode(email);
    }
  }

  /**
   * URL-encodes a value so it can be safely appended as a query parameter.
   *
   * @param value input text; null is converted to an empty string
   * @return UTF-8 encoded value
   */
  private String encode(String value) {
    return UriUtils.encode(value == null ? "" : value, StandardCharsets.UTF_8);
  }
}
