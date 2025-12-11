package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.javalin.Javalin;
import org.example.controller.ClientController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ClientViewTest {

    private static WebDriver driver;
    private static Javalin app;

    @BeforeAll
    static void setup() {
        WebDriverManager.chromedriver().setup();

        app = Javalin.create();
        new ClientController(app);
        app.start(7000);


        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    static void teardown() {
        driver.quit();
        app.stop();
    }

    @Test
    void testCreateClient() {
        driver.get("http://localhost:7000/clients");

        WebElement link = driver.findElement(By.linkText("Adicionar Novo Cliente"));
        link.click();

        driver.findElement(By.name("name")).sendKeys("João da Silva");
        driver.findElement(By.name("email")).sendKeys("joao@example.com");

        driver.findElement(By.cssSelector("form")).submit();

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/clients"));

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("João da Silva"));
    }
    @Test
    void testEditClient() {
        driver.get("http://localhost:7000/clients");

        WebElement link = driver.findElement(By.linkText("Editar"));
        link.click();

        WebElement nameInput = driver.findElement(By.name("name"));
        WebElement emailInput = driver.findElement(By.name("email"));

        nameInput.clear();
        emailInput.clear();

        nameInput.sendKeys("editedClient");
        emailInput.sendKeys("editedClient@example.com");

        driver.findElement(By.cssSelector("form")).submit();

        Assertions.assertTrue(driver.getCurrentUrl().endsWith("/clients"));

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("editedClient"));
    }


    @Test
    void testDeleteClient() {
        driver.get("http://localhost:7000/clients");

        WebElement deleteButton = driver.findElement(
                By.xpath("//tr[td[contains(text(), 'Alice')]]//button[contains(text(), 'Deletar') or contains(., 'Deletar')]")
        );
        deleteButton.click();

        Assertions.assertFalse(driver.getPageSource().contains("Alice"));
    }
}



