package com.storeshop.config;

import com.storeshop.entities.Categorie;
import com.storeshop.entities.Produit;
import com.storeshop.repositories.CategorieRepository;
import com.storeshop.repositories.ProduitRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds default categories and products when the database is empty.
 *
 * <p>Image URLs use deterministic picsum seeds so previews are stable and always resolvable.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

  private final CategorieRepository categorieRepository;
  private final ProduitRepository produitRepository;

  @Override
  public void run(String... args) {
    Categorie electronique = ensureCategory("Electronique");
    Categorie mode = ensureCategory("Mode");
    Categorie maison = ensureCategory("Maison");
    Categorie sport = ensureCategory("Sport");

    List<Produit> produits = new ArrayList<>();
    produits.add(
        product(
            electronique,
            "Laptop Pro 14",
            "Ordinateur portable leger pour travail et etudes.",
            1299.00,
            18,
            "https://picsum.photos/seed/storeshop-laptop-pro-14/1200/900"));
    produits.add(
        product(
            electronique,
            "Casque Bluetooth Noise-Free",
            "Casque sans fil avec reduction de bruit et autonomie 30h.",
            179.90,
            35,
            "https://picsum.photos/seed/storeshop-headphones-noisefree/1200/900"));
    produits.add(
        product(
            electronique,
            "Smartphone Nova X",
            "Ecran AMOLED, triple capteur photo et charge rapide.",
            749.00,
            26,
            "https://picsum.photos/seed/storeshop-smartphone-novax/1200/900"));

    produits.add(
        product(
            mode,
            "Veste Denim Urban",
            "Veste en jean coupe moderne, usage quotidien.",
            69.90,
            42,
            "https://picsum.photos/seed/storeshop-denim-jacket-urban/1200/900"));
    produits.add(
        product(
            mode,
            "Sneakers Aero Run",
            "Chaussures confortables pour ville et marche.",
            89.90,
            54,
            "https://picsum.photos/seed/storeshop-sneakers-aero-run/1200/900"));
    produits.add(
        product(
            mode,
            "Sac a Dos Minimal",
            "Sac resistant avec compartiment laptop 15 pouces.",
            49.90,
            60,
            "https://picsum.photos/seed/storeshop-backpack-minimal/1200/900"));

    produits.add(
        product(
            maison,
            "Lampe LED Ambiance",
            "Lampe design avec variation d'intensite lumineuse.",
            39.90,
            27,
            "https://picsum.photos/seed/storeshop-led-lamp-ambiance/1200/900"));
    produits.add(
        product(
            maison,
            "Chaise Ergonomique",
            "Chaise de bureau avec soutien lombaire reglable.",
            159.00,
            14,
            "https://picsum.photos/seed/storeshop-ergonomic-chair/1200/900"));
    produits.add(
        product(
            maison,
            "Set Cuisine Inox",
            "Set complet ustensiles inox pour cuisine quotidienne.",
            79.00,
            22,
            "https://picsum.photos/seed/storeshop-kitchen-set-inox/1200/900"));

    produits.add(
        product(
            sport,
            "Tapis Yoga Flex",
            "Tapis antiderapant pour yoga, pilates et stretching.",
            24.90,
            73,
            "https://picsum.photos/seed/storeshop-yoga-mat-flex/1200/900"));
    produits.add(
        product(
            sport,
            "Halteres 2x10kg",
            "Paire d'halteres pour renforcement musculaire maison.",
            59.90,
            19,
            "https://picsum.photos/seed/storeshop-dumbbells-2x10/1200/900"));
    produits.add(
        product(
            sport,
            "Montre Sport Pulse",
            "Suivi du rythme cardiaque, sommeil et activites.",
            129.00,
            31,
            "https://picsum.photos/seed/storeshop-sport-watch-pulse/1200/900"));

        int created = 0;
        int updated = 0;
        for (Produit produit : produits) {
            Produit existing = produitRepository.findByName(produit.getName());
            if (existing == null) {
                produitRepository.save(produit);
                created++;
            } else {
                existing.setCategorie(produit.getCategorie());
                existing.setDescription(produit.getDescription());
                existing.setPrice(produit.getPrice());
                existing.setStock(produit.getStock());
                existing.setImageUrl(produit.getImageUrl());
                produitRepository.save(existing);
                updated++;
            }
        }

        log.info("DataSeeder completed: {} created, {} updated.", created, updated);
  }

  private Categorie ensureCategory(String nom) {
    Categorie existing = categorieRepository.findByNom(nom);
    if (existing != null) {
      return existing;
    }
    Categorie categorie = new Categorie();
    categorie.setNom(nom);
    return categorieRepository.save(categorie);
  }

  private Produit product(
      Categorie categorie,
      String name,
      String description,
      double price,
      int stock,
      String imageUrl) {
    Produit produit = new Produit();
    produit.setCategorie(categorie);
    produit.setName(name);
    produit.setDescription(description);
    produit.setPrice(price);
    produit.setStock(stock);
    produit.setImageUrl(imageUrl);
    return produit;
  }
}
