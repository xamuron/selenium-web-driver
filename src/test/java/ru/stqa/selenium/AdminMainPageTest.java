package ru.stqa.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
        int i = 0;
        int j = 0;

        for (WebElement el: pages) {
            int finalI = i;
            // без ожидания не удалось делать клики по элементам, взял пример отсюда https://reflect.run/articles/how-to-deal-with-staleelementreferenceexception-in-selenium/
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .ignoring(StaleElementReferenceException.class)
                    .until((WebDriver d) -> {
                        List<WebElement> elements = d.findElements(By.id("app-"));
                        elements.get(finalI).click();
                        return true;
                    });
            i++;
            Assertions.assertTrue(Utils.isElementPresent(driver, By.cssSelector("h1")));

            // если у выбранного пунка меню есть подпункты, то проваливаемся в подпункты
            if (Utils.isElementPresent(driver, By.xpath("*//li[@class='selected'and @id='app-']/ul"))) {
                List<WebElement> subpages = driver.findElements(By.xpath("*//li[@class='selected'and @id='app-']/ul/li"));
                for (WebElement subp: subpages) {
                    int finalJ = j;
                    new WebDriverWait(driver, Duration.ofSeconds(10))
                            .ignoring(StaleElementReferenceException.class)
                            .until((WebDriver d) -> {
                                List<WebElement> elements = d.findElements(By.xpath("*//li[@class='selected'and @id='app-']/ul/li"));
                                elements.get(finalJ).click();
                                return true;
                            });
                    j++;
                    Assertions.assertTrue(Utils.isElementPresent(driver, By.cssSelector("h1")));
                }
                j = 0;
            }
        }
    }

    @AfterAll
    public void stop() {
        driver.quit();
        driver = null;
    }
}
