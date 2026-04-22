package com.storeshop.ui;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Tests d'interface utilisateur pour l'administration.
 * Type : Test Selenium / UI
 */
@Tag("Selenium")
class AdminDashboardUITest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu", "--no-sandbox", "--window-size=1920,1080");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Vérifie que l'accès au tableau de bord sans login redirige l'utilisateur")
    void testAdminAccessDeniedForGuest() {
        driver.get(BASE_URL + "/admin/dashboard");
        
        // Comme Spring Security protège ce point d'accès, l'utilisateur anonyme doit atterrir sur /login
        wait.until(ExpectedConditions.urlContains("/login"));
        
        String url = driver.getCurrentUrl();
        Assertions.assertTrue(url.contains("/login"), "Un utilisateur non-connecté doit être redirigé vers la connexion.");
    }

    @Test
    @DisplayName("Vérifie que l'accès aux commandes sans login bloque l'utilisateur")
    void testAdminOrdersDeniedForGuest() {
        driver.get(BASE_URL + "/admin/commandes");
        
        wait.until(ExpectedConditions.urlContains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "La sécurité doit bloquer un accès anonyme à /admin/commandes.");
    }
}
