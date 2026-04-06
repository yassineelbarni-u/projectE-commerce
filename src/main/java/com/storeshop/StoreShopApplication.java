package com.storeshop;

import com.storeshop.services.AccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
/**
 * Bootstrap class for the StoreShop Spring Boot application.
 */
public class StoreShopApplication {

  /**
   * Application entry point.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(StoreShopApplication.class, args);
  }

  /**
   * Initializes a default admin account when the app starts.
   *
   * <p>Values can be overridden through properties:
   * {@code app.admin.username/app.admin.password/app.admin.email}.
   *
   * @param accountService service used to create or load admin user
   * @param adminUsername configured admin username
   * @param adminPassword configured admin password
   * @param adminEmail configured admin email
   * @return startup callback executed once after Spring context initialization
   */
  @Bean
  CommandLineRunner initAdmin(
      AccountService accountService,
      @Value("${app.admin.username:admin}") String adminUsername,
      @Value("${app.admin.password:admin123}") String adminPassword,
      @Value("${app.admin.email:admin@storeshop.local}") String adminEmail) {
    return args -> accountService.ensureUserExists(adminUsername, adminPassword, adminEmail);
  }
}
