package fstm.ilisi.Gestion_bibliotheque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fstm.ilisi.Gestion_bibliotheque.entity.Produit;
import fstm.ilisi.Gestion_bibliotheque.repository.ProduitRepository;
import fstm.ilisi.Gestion_bibliotheque.service.AccountService;

@SpringBootApplication
public class GestionBibliothequeApplication implements CommandLineRunner {

	  @Autowired
      private ProduitRepository produitRepository;

	public static void main(String[] args) {
		SpringApplication.run(GestionBibliothequeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		// produitRepository.save(new Produit(null, "Smartphone Samsung", "Electronique", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9", "Téléphone haut de gamme 128GB", 599.99, 10));
		// produitRepository.save(new Produit(null, "Ordinateur portable HP", "Informatique", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853", "PC portable 15 pouces, 8GB RAM", 899.99, 5));
		// produitRepository.save(new Produit(null, "Écouteurs Bluetooth", "Audio", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e", "Écouteurs sans fil avec réduction de bruit", 79.99, 20));
		// produitRepository.save(new Produit(null, "Tablette iPad", "Electronique", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0", "Tablette 10 pouces, 64GB", 449.99, 8));
	}

	@Bean

	CommandLineRunner CommandeLineRunnerDetails(AccountService accountService) { 
		return args -> {
			accountService.AddRole("USER");
			accountService.AddRole("ADMIN");
			accountService.AddUser("user1", "1234", "user1@gmail.com", "1234");
			accountService.AddUser("user2", "1234", "user2@gmail.com", "1234");
			accountService.AddUser("admin", "1234", "admin@gmail.com", "1234");

			accountService.AddRoleToUser("user1", "USER");
			accountService.AddRoleToUser("user2", "USER");
			accountService.AddRoleToUser("admin", "USER");
			accountService.AddRoleToUser("admin", "ADMIN");

		};
	}
}
