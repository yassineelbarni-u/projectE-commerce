package com.storeshop.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.services.CategorieService;
import com.storeshop.services.ProduitService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin/produits")
@AllArgsConstructor
public class ProduitController {
    
    // Injection de dépendances
    private final ProduitService produitService;
    private final CategorieService categorieService;
    
    // Chemin pour stocker les images uploadées
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    
    // Afficher la liste des produits avec pagination et recherche
    @GetMapping
    public String index(Model model,
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
    public String deleteProduit(@RequestParam(name = "id") Long id,
                          @RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "search", defaultValue = "") String search) {
        try {
            produitService.deleteProduit(id);
            return "redirect:/admin/produits?page=" + page + "&search=" + search;
        } catch (RuntimeException e) {
            return "redirect:/admin/produits?page=" + page + "&search=" + search + "&error=" + e.getMessage();
        }
    }
    
    // Afficher le formulaire d'édition d'un produit
    @GetMapping("/edit")
    public String showEditForm(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "search", defaultValue = "") String search,
                               Model model) {
        Produit produit = produitService.getProduitById(id);
        List<Categorie> categories = categorieService.getAllCategories();
        
        model.addAttribute("produit", produit);
        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        return "produit/editProduit";
    }
    
    // methode pour sauvegarder la modification d'un produit
    @PostMapping("/edit")
    @SuppressWarnings("CallToPrintStackTrace")
    public String saveProduit(@ModelAttribute Produit produit,
                            @RequestParam(name = "categorieId", required = false) Long categorieId,
                            @RequestParam(name = "search", defaultValue = "") String search,
                            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {
        
        // Associer la catégorie au produit
        if (categorieId != null) {
            Categorie categorie = categorieService.getCategorieById(categorieId);

            // Associer la categorie au produit
            produit.setCategorie(categorie);
        }
        
        // Gérer l'upload de l'image si un fichier est fourni
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = saveImageFile(imageFile);
                produit.setImageUrl("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                
            }
        }
        
        try {
            produitService.saveProduit(produit);
            return "redirect:/admin/produits?search=" + search;
        } catch (RuntimeException e) {
            return "redirect:/admin/produits/edit?id=" + produit.getId() + "&search=" + search + "&error=" + e.getMessage();
        }
    }

    @GetMapping("/add")
    public String showAddForm(@RequestParam(name = "search", defaultValue = "") String search,
                            Model model) {
        List<Categorie> categories = categorieService.getAllCategories();
        model.addAttribute("produit", new Produit());
        model.addAttribute("categories", categories);
        model.addAttribute("search", search);
        return "produit/ajouterProduit";
    }

    @PostMapping("/add")
    @SuppressWarnings("CallToPrintStackTrace")
    public String addProduit(@ModelAttribute Produit produit,
                            @RequestParam(name = "categorieId", required = false) Long categorieId,
                            @RequestParam(name = "search", defaultValue = "") String search,
                            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile) {
        
        // Associer la catégorie au produit
        if (categorieId != null) {
            Categorie categorie = categorieService.getCategorieById(categorieId);
            produit.setCategorie(categorie);
        }
        
        // Gérer l'upload de l'image si un fichier est fourni
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = saveImageFile(imageFile);
                produit.setImageUrl("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
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
    
    // Méthode pour sauvegarder l'image uploadée
    private String saveImageFile(MultipartFile file) throws IOException {
        // Créer le dossier s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Générer un nom de fichier unique
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        
        // Sauvegarder le fichier
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

}
