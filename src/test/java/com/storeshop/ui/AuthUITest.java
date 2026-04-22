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
 * Tests d'interface utilisateur pour l'authentification.
 * Type : Test Selenium / UI
 */
@Tag("Selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthUITest {

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
    @DisplayName("Vérifie qu'une connexion invalide affiche une erreur")
    void testInvalidLoginShowsError() {
        driver.get(baseUrl() + "/login");
        
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        
        usernameInput.sendKeys("utilisateur_inconnu");
        passwordInput.sendKeys("mauvais_mot_de_passe");
        submitButton.click();
        
        wait.until(ExpectedConditions.urlContains("error"));
        
    WebElement errorAlert = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".login-alert.danger")));
        Assertions.assertNotNull(errorAlert, "Un message d'erreur doit être affiché");
    }

    @Test
    @DisplayName("Vérifie que le formulaire d'inscription a les bons champs")
    void testRegistrationFormFields() {
        driver.get(baseUrl() + "/register");
        
        Assertions.assertNotNull(driver.findElement(By.id("username")), "Le champ username doit exister");
        Assertions.assertNotNull(driver.findElement(By.id("email")), "Le champ email doit exister");
        Assertions.assertNotNull(driver.findElement(By.id("password")), "Le champ password doit exister");
    }
}
