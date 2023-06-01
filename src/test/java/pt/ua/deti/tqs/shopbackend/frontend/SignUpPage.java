package pt.ua.deti.tqs.shopbackend.frontend;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignUpPage {

    @FindBy(xpath = "/html/body/div/div[2]/form/div/input[1]")
    private WebElement emailInput;

    @FindBy(xpath = "/html/body/div/div[2]/form/div/input[2]")
    private WebElement passwordInput;

    @FindBy(xpath = "/html/body/div/div[2]/form/div/button")
    private WebElement signInInput;



    public SignUpPage(WebDriver driver) {
        PageFactory.initElements(driver,this);
    }

    public void fillEmail(String email) {
        emailInput.sendKeys(email);
    }

    public void fillPassword(String password) {
        passwordInput.sendKeys(password);
    }

    public void submit() {
        signInInput.click();
    }

}
