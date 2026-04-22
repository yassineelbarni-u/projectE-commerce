package com.storeshop.ui;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests d'interface utilisateur utilisant Selenium et WebDriverManager.
 * Type : Test Selenium / UI
 * Ces tests valident le fonctionnement global depuis le navigateur.
 */
@Tag("Selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StoreFrontUITest {

    private WebDriver driver;
    private WebDriverWait wait;

    @LocalServerPort
    private int port;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Exécution en mode headless pour l'intégration continue
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");

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
    @DisplayName("Vérifie le chargement de la page d'accueil via Selenium")
    void testHomePageLoads() {
        driver.get(baseUrl() + "/");
        Assertions.assertTrue(driver.getTitle().contains("StoreShop") || driver.getTitle().contains("Catalogue"), 
                              "Le titre de la page d'accueil doit contenir StoreShop ou Catalogue");
        
        WebElement productCatalog = driver.findElement(By.cssSelector(".pub-section"));
        Assertions.assertNotNull(productCatalog, "La section principale des produits doit être présente.");
    }

    @Test
    @DisplayName("Vérifie la navigation vers la page de login")
    void testNavigateToLogin() {
        driver.get(baseUrl() + "/");
        WebElement loginLink = driver.findElement(By.cssSelector("a[href='/login']"));
        loginLink.click();
        
        wait.until(ExpectedConditions.urlContains("/login"));
        
        WebElement loginForm = driver.findElement(By.tagName("form"));
        Assertions.assertNotNull(loginForm, "Le formulaire de connexion doit être présent.");
    }

    @Test
    @DisplayName("Vérifie la navigation vers la création de compte")
    void testNavigateToRegister() {
        driver.get(baseUrl() + "/login");
        WebElement registerLink = driver.findElement(By.cssSelector("a[href='/register']"));
        registerLink.click();
        
        wait.until(ExpectedConditions.urlContains("/register"));
        
        WebElement registerForm = driver.findElement(By.tagName("form"));
        Assertions.assertNotNull(registerForm, "Le formulaire d'inscription doit être présent.");
    }

    @Test
    @DisplayName("Vérifie le fonctionnement de la barre de recherche des produits")
    void testProductSearch() {
        driver.get(baseUrl() + "/");
        
        WebElement searchInput = driver.findElement(By.name("search"));
        
        searchInput.sendKeys("Test");
        searchInput.submit();
        
        // Vérifie que la requête de recherche est bien prise en compte dans l'URL
        wait.until(ExpectedConditions.urlContains("search=Test"));
    }

    @Test
    @DisplayName("Vérifie l'accès à la page panier")
    void testAccessCart() {
        driver.get(baseUrl() + "/panier");
        
        wait.until(ExpectedConditions.urlContains("/login"));
        
        WebElement loginForm = driver.findElement(By.tagName("form"));
        Assertions.assertNotNull(loginForm,
                "Un utilisateur anonyme doit être redirigé vers le formulaire de connexion pour accéder au panier.");
    }
}
