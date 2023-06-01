package pt.ua.deti.tqs.shopbackend.frontend;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BookPage {

    @FindBy(xpath = "/html/body/div/div[2]/div[2]/div[2]/button")
    private WebElement addItemButton;

    @FindBy(xpath = "/html/body/div/div[1]/div[2]/div[2]/label")
    private WebElement cartButton;

    @FindBy(xpath = "/html/body/div/div[1]/div[2]/div[2]/div/div/span[1]")
    private WebElement numberItemLabel;

    @FindBy(xpath = "/html/body/div/div[1]/div[2]/div[2]/div/div/div/a")
    private WebElement viewCartButton;

    public  BookPage(WebDriver driver) {
        PageFactory.initElements(driver,this);
    }

    public void addItem() {
        addItemButton.click();
    }

    public void openCartButton() {
        cartButton.click();
    }

    public String getTotalItem() {
        return numberItemLabel.getText();
    }

    public void viewCart() {
        viewCartButton.click();
    }
}
