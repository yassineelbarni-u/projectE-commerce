package com.storeshop.controllers;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.services.CategorieService;
import com.storeshop.services.ProduitService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/produits")
@RequiredArgsConstructor
/**
 * Admin web controller for product management.
 *
 * <p>This class handles list/search pages, create/update forms, and optional image upload. It is
 * designed for server-rendered pages (template names and redirects), not JSON APIs.
 */
public class ProduitController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProduitController.class);
    /** MIME types accepted for uploaded product images. */
  private static final List<String> ALLOWED_CONTENT_TYPES =
      List.of("image/jpeg", "image/png", "image/gif", "image/webp");
    /** Mapping from MIME type to file extension used when generating filenames. */
  private static final Map<String, String> CONTENT_TYPE_EXTENSIONS =
      Map.of(
          "image/jpeg", ".jpg",
          "image/png", ".png",
          "image/gif", ".gif",
          "image/webp", ".webp");

  private final ProduitService produitService;
  private final CategorieService categorieService;

  /** Directory where uploaded product images are saved. */
  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  /**
   * Displays the paginated products list for admins.
   *
   * @param model MVC model populated for the template
   * @param page zero-based page index
   * @param size number of rows per page
   * @param search text filter applied to name/description/category
   * @return products list template
   */
  @GetMapping
  public String index(
      Model model,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "5") int size,
      @RequestParam(name = "search", defaultValue = "") String search) {

    Page<Produit> produitsPage = produitService.searchProduits(search, page, size);
    model.addAttribute("ListeProduit", produitsPage.getContent());
    model.addAttribute("pages", new int[produitsPage.getTotalPages()]);
    model.addAttribute("currentPage", page);
    model.addAttribute("search", search);
    return "produit/ListeProduit";
  }

  /**
   * Deletes a product then returns to the list while preserving current pagination/search context.
   *
   * @param id product id to delete
   * @param page page index to restore in the redirect
   * @param search active search text to restore in the redirect
   * @return redirect URL to list page, with optional error message
   */
  @GetMapping("/delete")
  public String deleteProduit(
      @RequestParam(name = "id") Long id,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "search", defaultValue = "") String search) {
    try {
      produitService.deleteProduit(id);
      return "redirect:/admin/produits?page=" + page + "&search=" + search;
    } catch (RuntimeException e) {
      return "redirect:/admin/produits?page="
          + page
          + "&search="
          + search
          + "&error="
          + e.getMessage();
    }
  }

  /**
   * Opens the product edit form.
   *
   * @param id product id
   * @param search current search text (kept for navigation continuity)
   * @param model MVC model containing the product and all categories
   * @return edit product template
   */
  @GetMapping("/edit")
  public String showEditForm(
      @RequestParam(name = "id") Long id,
      @RequestParam(name = "search", defaultValue = "") String search,
      Model model) {
    Produit produit = produitService.getProduitById(id);
    List<Categorie> categories = categorieService.getAllCategories();

    model.addAttribute("produit", produit);
    model.addAttribute("categories", categories);
    model.addAttribute("search", search);

    return "produit/editProduit";
  }

  /**
   * Persists product edits and optionally replaces its image.
   *
   * @param produit product data bound from form fields
   * @param categorieId optional category id selected in the form
   * @param search search text to keep in redirect URL
   * @param imageFile optional uploaded image
   * @return redirect URL to list page or back to edit page with error details
   */
  @PostMapping("/edit")
  public String saveProduit(
      @ModelAttribute Produit produit,
      @RequestParam(name = "categorieId", required = false) Long categorieId,
      @RequestParam(name = "search", defaultValue = "") String search,
      @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {

    if (categorieId != null) {
      Categorie categorie = categorieService.getCategorieById(categorieId);
      produit.setCategorie(categorie);
    }

    if (imageFile != null && !imageFile.isEmpty()) {
      try {
        String fileName = saveImageFile(imageFile);
        produit.setImageUrl("/uploads/" + fileName);
      } catch (IOException e) {
        LOGGER.warn("Image upload failed during product update", e);
      }
    }

    try {
      produitService.saveProduit(produit);
      return "redirect:/admin/produits?search=" + search;
    } catch (RuntimeException e) {
      return "redirect:/admin/produits/edit?id="
          + produit.getId()
          + "&search="
          + search
          + "&error="
          + e.getMessage();
    }
  }

  /**
   * Opens the add-product form.
   *
   * @param search current search text
   * @param model MVC model containing an empty product and category choices
   * @return add product template
   */
  @GetMapping("/add")
  public String showAddForm(
      @RequestParam(name = "search", defaultValue = "") String search, Model model) {
    List<Categorie> categories = categorieService.getAllCategories();
    model.addAttribute("produit", new Produit());
    model.addAttribute("categories", categories);
    model.addAttribute("search", search);
    return "produit/ajouterProduit";
  }

  /**
   * Creates a new product and optionally stores an uploaded image.
   *
   * @param produit product data bound from form fields
   * @param categorieId optional category id selected in the form
   * @param search search text to keep in redirect URL
   * @param imageFile optional uploaded image
   * @return redirect URL to list page or back to add page with error details
   */
  @PostMapping("/add")
  public String addProduit(
      @ModelAttribute Produit produit,
      @RequestParam(name = "categorieId", required = false) Long categorieId,
      @RequestParam(name = "search", defaultValue = "") String search,
      @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {

    if (categorieId != null) {
      Categorie categorie = categorieService.getCategorieById(categorieId);
      produit.setCategorie(categorie);
    }

    if (imageFile != null && !imageFile.isEmpty()) {
      try {
        String fileName = saveImageFile(imageFile);
        produit.setImageUrl("/uploads/" + fileName);
      } catch (IOException e) {
        LOGGER.warn("Image upload failed during product creation", e);
      }
    }

    try {
      produitService.saveProduit(produit);
      return "redirect:/admin/produits?search=" + search;
    } catch (RuntimeException e) {
      return "redirect:/admin/produits/add?search=" + search + "&error=" + e.getMessage();
    }
  }

  @GetMapping("/")
  public String home() {
    return "redirect:/admin/dashboard";
  }

  /**
   * Stores an uploaded image file on disk and returns the generated filename.
   *
   * <p>The method validates file type, creates the upload folder if missing, and writes the file
   * with a random UUID filename to avoid collisions.
   *
   * @param file uploaded multipart file
   * @return generated filename (without path), for example {@code f81d4fae-... .jpg}
   * @throws IOException when the file type is unsupported or disk write fails
   */
  private String saveImageFile(MultipartFile file) throws IOException {
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new IOException("Unsupported file type");
    }

    Path uploadPath = Paths.get(uploadDir);
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    String extension = CONTENT_TYPE_EXTENSIONS.get(contentType);
    if (extension == null) {
      throw new IOException("Unsupported file type");
    }
    String fileName = UUID.randomUUID().toString() + extension;

    Path filePath = uploadPath.resolve(fileName);
    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    return fileName;
  }
}
