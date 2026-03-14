package fstm.ilisi.Gestion_bibliotheque.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import fstm.ilisi.Gestion_bibliotheque.entity.Produit;
import fstm.ilisi.Gestion_bibliotheque.service.AccountService;
import fstm.ilisi.Gestion_bibliotheque.service.CategorieService;
import fstm.ilisi.Gestion_bibliotheque.service.ProduitService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PublicController {

    private final ProduitService produitService;
    private final CategorieService categorieService;
    private final AccountService accountService;

    @GetMapping({"/", "/home", "/produits"})
    public String home(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "8") int size,
                       @RequestParam(name = "search", defaultValue = "") String search,
                       @RequestParam(name = "categorieId", required = false) Long categorieId) {

        Page<Produit> produitsPage = produitService.searchProduitsPublic(search, categorieId, page, size);
        model.addAttribute("ListeProduit", produitsPage.getContent());
        model.addAttribute("pages", new int[produitsPage.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("selectedCategorieId", categorieId);
        model.addAttribute("categories", categorieService.getAllCategories());
        return "public/home";
    }

    @GetMapping("/produits/detail")
    public String showProduitDetail(@RequestParam(name = "id") Long id, Model model) {
        model.addAttribute("produit", produitService.getProduitById(id));
        return "public/detail-produit";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "public/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword) {
        try {
            accountService.ensureRoleExists("CLIENT");
            accountService.AddUser(username, password, email, confirmPassword);
            accountService.AddRoleToUser(username, "CLIENT");
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            return "redirect:/register?error=" + encode(e.getMessage())
                    + "&username=" + encode(username)
                    + "&email=" + encode(email);
        }
    }

    private String encode(String value) {
        return UriUtils.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}