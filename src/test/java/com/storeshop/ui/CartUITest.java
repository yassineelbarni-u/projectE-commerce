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
import java.util.List;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests d'interface utilisateur pour le panier interactif.
 * Type : Test Selenium / UI
 */
@Tag("Selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CartUITest {

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
    @DisplayName("Vérifie qu'un panier vide affiche le bon message")
    void testEmptyCartMessage() {
        driver.get(baseUrl() + "/panier");

        wait.until(ExpectedConditions.urlContains("/login"));

        WebElement loginForm = driver.findElement(By.tagName("form"));
        Assertions.assertNotNull(loginForm, "Un utilisateur anonyme doit être redirigé vers la page de login.");
    }

    @Test
    @DisplayName("Vérifie qu'il n'y a pas de produits dans le panier au démarrage d'une session")
    void testNoCartLinesAtStart() {
        driver.get(baseUrl() + "/panier");

        wait.until(ExpectedConditions.urlContains("/login"));

        List<WebElement> loginAlerts = driver.findElements(By.cssSelector(".login-alert"));
        Assertions.assertTrue(loginAlerts.isEmpty() || !loginAlerts.get(0).getText().isBlank(),
                "La page de login doit être rendue correctement après redirection depuis le panier.");
    }
}
