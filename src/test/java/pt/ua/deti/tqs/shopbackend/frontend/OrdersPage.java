package pt.ua.deti.tqs.shopbackend.frontend;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pt.ua.deti.tqs.shopbackend.model.enums.Status;

import java.time.Duration;
import java.util.List;

public class OrdersPage {
    private WebDriver driver;
    private WebDriverWait webDriverWait;

    @FindBy(xpath = "/html/body/div/div[2]/h1")
    private WebElement titleOrders;

    @FindBy(xpath = "/html/body/div/div[2]/div[1]/div[2]/div")
    private WebElement statusOrder;

    @FindBy(xpath = "/html/body/div/div[2]/div[1]")
    private WebElement cardOrder;

    public OrdersPage(WebDriver driver) {
        this.driver = driver;
        this.webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(50));
        PageFactory.initElements(driver,this);
    }

    public String getTitle() {
        return titleOrders.getText();
    }

    public Boolean getStatus() {
        List<Status> status = List.of(Status.values());
        if(statusOrder.getText() == null || statusOrder.getText().isEmpty()) {
            return false;
        }
        return status.contains(Status.valueOf(statusOrder.getText()));
    }

    public void waitcardOrder() {
        webDriverWait.until(d -> cardOrder.isDisplayed());
        cardOrder.click();
    }
}
