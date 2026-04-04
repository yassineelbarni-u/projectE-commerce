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
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ProduitController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProduitController.class);
  private static final List<String> ALLOWED_CONTENT_TYPES =
      List.of("image/jpeg", "image/png", "image/gif", "image/webp");
  private static final Map<String, String> CONTENT_TYPE_EXTENSIONS =
      Map.of(
          "image/jpeg", ".jpg",
          "image/png", ".png",
          "image/gif", ".gif",
          "image/webp", ".webp");

  // Dependency injection
  private final ProduitService produitService;
  private final CategorieService categorieService;

  // Path to store uploaded images
  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  // Display the list of products with pagination and search
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

  // Display the product edit form
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

  // Method to save product modification
  @PostMapping("/edit")
  public String saveProduit(
      @ModelAttribute Produit produit,
      @RequestParam(name = "categorieId", required = false) Long categorieId,
      @RequestParam(name = "search", defaultValue = "") String search,
      @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {

    // Associate the category to the product
    if (categorieId != null) {
      Categorie categorie = categorieService.getCategorieById(categorieId);

      // Associate the category to the product
      produit.setCategorie(categorie);
    }

    // Handle image upload if a file is provided
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

  @GetMapping("/add")
  public String showAddForm(
      @RequestParam(name = "search", defaultValue = "") String search, Model model) {
    List<Categorie> categories = categorieService.getAllCategories();
    model.addAttribute("produit", new Produit());
    model.addAttribute("categories", categories);
    model.addAttribute("search", search);
    return "produit/ajouterProduit";
  }

  @PostMapping("/add")
  public String addProduit(
      @ModelAttribute Produit produit,
      @RequestParam(name = "categorieId", required = false) Long categorieId,
      @RequestParam(name = "search", defaultValue = "") String search,
      @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {

    // Associate the category to the product
    if (categorieId != null) {
      Categorie categorie = categorieService.getCategorieById(categorieId);
      produit.setCategorie(categorie);
    }

    // Handle image upload if a file is provided
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

  // Method to save uploaded image
  private String saveImageFile(MultipartFile file) throws IOException {
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new IOException("Unsupported file type");
    }

    // Create the directory if it does not exist
    Path uploadPath = Paths.get(uploadDir);
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // Generate a unique file name
    String extension = CONTENT_TYPE_EXTENSIONS.get(contentType);
    if (extension == null) {
      throw new IOException("Unsupported file type");
    }
    String fileName = UUID.randomUUID().toString() + extension;

    // Save the file
    Path filePath = uploadPath.resolve(fileName);
    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

    return fileName;
  }
}
