package ru.stqa.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LiteCartMailPageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    public void start() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        //использование неявных ожиданий
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void liteCartMailPageTest() {
        driver.get("http://localhost/litecart/en/");

        List<WebElement> products = driver.findElements(By.cssSelector("li.product"));
        for (WebElement el:
             products) {
            List<WebElement> stickers = el.findElements(By.cssSelector("div.sticker"));
            Assertions.assertEquals(1, stickers.size());
            System.out.println(stickers.get(0).getText());
        }
    }

    @AfterAll
    public void stop() {
        driver.quit();
        driver = null;
    }
}
