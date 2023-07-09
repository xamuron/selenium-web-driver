package ru.stqa.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminMainPageTest {
    private WebDriver driver;
    private WebDriverWait wait;

    public static final String adminLogin = "admin";
    public static final String adminPassword = "admin";

    @BeforeAll
    public void start() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
    }

    public void login(String user, String pass) {
        driver.get("http://localhost/litecart/admin/");
        driver.findElement(By.name("username")).sendKeys(user);
        driver.findElement(By.name("password")).sendKeys(pass);
        driver.findElement(By.name("login")).click();
    }

    @Test
    public void adminMainPageTest() {
        login(adminLogin, adminPassword);

        List<WebElement> pages = driver.findElements(By.id("app-"));
        pages.get(0).click();

        for (int i = 0; i < pages.size() - 1; i++) {
            List<WebElement> elements = driver.findElements(By.id("app-"));
            elements.get(i).click();
            Assertions.assertTrue(Utils.isElementPresent(driver, By.cssSelector("h1")));

            // если у выбранного пунка меню есть подпункты, то проваливаемся в подпункты
            if (Utils.isElementPresent(driver, By.xpath("*//li[@class='selected'and @id='app-']/ul"))) {
                List<WebElement> subpages = driver.findElements(By.xpath("*//li[@class='selected'and @id='app-']/ul/li"));
                for (int j = 0; j < subpages.size() - 1; j ++) {
                    List<WebElement> subElements = driver.findElements(By.xpath("*//li[@class='selected'and @id='app-']/ul/li"));
                    subElements.get(j).click();
                    Assertions.assertTrue(Utils.isElementPresent(driver, By.cssSelector("h1")));
                }
            }
        }
    }

    @AfterAll
    public void stop() {
        driver.quit();
        driver = null;
    }
}
