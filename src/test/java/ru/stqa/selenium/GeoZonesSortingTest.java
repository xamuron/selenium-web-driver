package ru.stqa.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeoZonesSortingTest {
    private WebDriver driver;
    private WebDriverWait wait;

    public static final String adminLogin = "admin";
    public static final String adminPassword = "admin";

    public void login(String user, String pass) {
        driver.get("http://localhost/litecart/admin/");
        driver.findElement(By.name("username")).sendKeys(user);
        driver.findElement(By.name("password")).sendKeys(pass);
        driver.findElement(By.name("login")).click();
    }

    @BeforeAll
    public void start() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        //использование неявных ожиданий
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    boolean hasZone(WebElement countryElement) {
        if (!countryElement.findElement(By.xpath("./td[6]")).getText().equals("0")) {
            return true;
        }
        return false;
    }

    @Test
    public void countriesSortingTest() {
        login(adminLogin, adminPassword);
        driver.get("http://localhost/litecart/admin/?app=geo_zones&doc=geo_zones");

        List<WebElement> zonesElements = driver.findElements(By.cssSelector("tr.row"));

        for (int i = 0; i <= zonesElements.size() - 1; i++) {
            driver.findElements(By.cssSelector("tr.row")).get(i).findElement(By.xpath("./td[3]/a")).click();

            // немного сложный локатор, но в нем сразу отсутствует хедер и строка с добавлением зоны
            List<WebElement> geoZonesElements = driver.findElements(By.xpath(".//table[@id='table-zones']//tr[.//a[@title='Remove']]"));
            List<String> geoZones = new ArrayList<>();
            // заполняем массив геозон выбранными(selected) значениями для каждого селекта
            for (WebElement element : geoZonesElements) {
                geoZones.add(element.findElement(By.xpath(".//td[3]/select/option[@selected]")).getText());
            }
            // получаем отсортированный список зон
            List<String> sortedGeoZones = new ArrayList<>();
            sortedGeoZones.addAll(geoZones);
            Collections.sort(sortedGeoZones);

            Assertions.assertArrayEquals(sortedGeoZones.toArray(), geoZones.toArray());

            driver.navigate().back();
        }
    }

    @AfterAll
    public void stop() {
        driver.quit();
        driver = null;
    }
}
