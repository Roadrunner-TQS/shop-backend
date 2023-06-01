package pt.ua.deti.tqs.shopbackend.frontend;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    @FindBy(xpath = "/html/body/div/div[1]/div[2]/a")
    private WebElement signInButton;

    @FindBy(xpath = "/html/body/div/div[2]/a[1]")
    private WebElement firstBook;

    @FindBy(xpath = "/html/body/div/div[1]/div[2]/div[2]/ul/li[1]/a")
    private WebElement ordersButton;

    @FindBy(xpath = "/html/body/div/div[1]/div[2]/div[2]/label")
    private WebElement wishlistButton;


    public HomePage(WebDriver driver) {
        String URL = "http://localhost:5173";
        driver.get(URL);
        PageFactory.initElements(driver,this);
    }

    public void clickSignIn() {
        signInButton.click();
    }

    public void clickFirstBook() {
        firstBook.click();
    }

    public void clickOrders() {
        wishlistButton.click();
        ordersButton.click();
    }
}
