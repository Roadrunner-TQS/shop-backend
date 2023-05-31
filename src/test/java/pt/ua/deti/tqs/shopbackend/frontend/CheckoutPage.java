package pt.ua.deti.tqs.shopbackend.frontend;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckoutPage {

    @FindBy(xpath = "/html/body/div/div[2]/form/div/input[1]")
    private WebElement inputCardNumber;

    @FindBy(xpath = "/html/body/div/div[2]/form/div/input[2]")
    private WebElement inputCardName;

    @FindBy(xpath = "/html/body/div/div[2]/form/div/input[3]")
    private WebElement inputCardExpiration;

    @FindBy(xpath = "/html/body/div/div[2]/form/div/input[4]")
    private WebElement inputCardCvv;

    @FindBy(xpath = "/html/body/div/div[2]/form/div/select/option[2]")
    private WebElement selectPickup;

    @FindBy(xpath = "/html/body/div/div[2]/form/div/button")
    private WebElement submitButton;

    public CheckoutPage(WebDriver driver) {
        PageFactory.initElements(driver,this);
    }



    public void fillCheckoutForm(long cardNumber, String cardName, String cardExpiration, int cardCvv) {
        inputCardNumber.sendKeys(String.valueOf(cardNumber));
        inputCardName.sendKeys(cardName);
        inputCardExpiration.sendKeys(cardExpiration);
        inputCardCvv.sendKeys(String.valueOf(cardCvv));
    }

    public void selectPickup() {
        selectPickup.click();
    }

    public void submit() {
        submitButton.click();
    }
}
