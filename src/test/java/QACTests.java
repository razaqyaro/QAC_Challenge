import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QACTests {
    public static void main(String[] args) throws InterruptedException {

        WebDriverManager.chromedriver().setup();

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.get("https://demo.nopcommerce.com/Desktop");
        WebElement currencyDropdown = driver.findElement(By.id("customerCurrency"));

        Select dropdown = new Select(currencyDropdown);
        dropdown.selectByVisibleText("Euro");
        Thread.sleep(30000);
        driver.findElement(By.id("small-searchterms")).sendKeys("Desktop");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(9000);


        List<WebElement> itemBoxes = driver.findElements(By.cssSelector(".item-box"));
        List<Item> items = new ArrayList<>();


        for (WebElement itemBox : itemBoxes) {
            WebElement priceElement = itemBox.findElement(By.cssSelector(".price.actual-price"));
            String priceText = priceElement.getText().replace("€", "").replace(",", ""); // Remove $ and , for parsing

            double price = Double.parseDouble(priceText);

            items.add(new Item(itemBox, price));
        }

        Collections.sort(items, Comparator.comparingDouble(Item::getPrice).reversed());

        if (items.size() >= 3) {
            items.get(1).getElement().findElement(By.cssSelector("button[title='Add to compare list']")).click(); // Second most expensive
            TimeUnit.SECONDS.sleep(2);
            items.get(2).getElement().findElement(By.cssSelector("button[title='Add to compare list']")).click(); // Third most expensive
        }
        Thread.sleep(5000);
        driver.findElement(By.cssSelector("a[href='/compareproducts']")).click();
        Thread.sleep(8000);
    }

    static class Item {
        private WebElement element;
        private double price;

        public Item(WebElement element, double price) {
            this.element = element;
            this.price = price;
        }

        public WebElement getElement() {
            return element;
        }

        public double getPrice() {
            return price;
        }
    }

}

