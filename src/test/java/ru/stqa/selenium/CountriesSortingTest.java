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
public class CountriesSortingTest {
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
        driver.get("http://localhost/litecart/admin/?app=countries&doc=countries");

        List<WebElement> countriesElements = driver.findElements(By.cssSelector("tr.row"));
        List<String> countries = new ArrayList<>();

        for (int i = 0; i < countriesElements.size() - 1; i++) {
            countries.add(countriesElements.get(i).findElement(By.xpath("./td[5]/a")).getText());
            // если у элементы страны есть Зоны, проваливаемся в страну и проверяем сортировку Зон
            if (hasZone(countriesElements.get(i))) {
                countriesElements.get(i).findElement(By.xpath(".//a")).click();

                List<WebElement> zonesElements = driver.findElements(By.cssSelector("table#table-zones tr"));
                // не учитываем хедер и последнюю строку с инпутом добавления зон
                zonesElements.remove(0);
                zonesElements.remove(zonesElements.size() - 1);
                List<String> zones = new ArrayList<>();

                for (WebElement elem:zonesElements) {
                    zones.add(elem.findElement(By.xpath("./td[3]")).getText());
                }
                List<String> sortedZones = new ArrayList<>();
                sortedZones.addAll(zones);
                Collections.sort(sortedZones);

                //проверям, что зоны отсортированы по алфавиту
                Assertions.assertArrayEquals(sortedZones.toArray(), zones.toArray());
                driver.navigate().back();
                // получаем заново список элементов стран после загрузки новый страницы
                countriesElements = driver.findElements(By.cssSelector("tr.row"));
            }
        }
        List<String> sortedCountries = new ArrayList<>();
        sortedCountries.addAll(countries);
        Collections.sort(sortedCountries);

        //проверям, что страны отсортированы по алфавиту
        Assertions.assertArrayEquals(sortedCountries.toArray(), countries.toArray());
    }

    @AfterAll
    public void stop() {
        driver.quit();
        driver = null;
    }
}
