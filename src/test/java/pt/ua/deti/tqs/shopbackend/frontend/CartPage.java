package pt.ua.deti.tqs.shopbackend.frontend;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.assertFalse;

public class CartPage {


    @FindBy(xpath = "/html/body/div/div[2]/div[1]/div[1]")
    private WebElement item;

    @FindBy(xpath = "/html/body/div/div[2]/div[3]/a")
    private WebElement checkoutButton;

    @FindBy(xpath = "/html/body/div/div[2]/div[1]/div[5]/button")
    private WebElement removeButton;

    @FindBy(xpath = "/html/body/div/div[2]/div[3]/div/div[2]")
    private WebElement total;


    public CartPage(WebDriver driver) {
        PageFactory.initElements(driver,this);
    }

    public String getItem() {
        return item.getText();
    }

    public void clickCheckoutButton() {
        checkoutButton.click();
    }

    public void clickRemoveButton() {
        removeButton.click();
    }

    public String getTotal() {
        return total.getText();
    }
}
