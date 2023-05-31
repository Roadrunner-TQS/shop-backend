package pt.ua.deti.tqs.shopbackend.frontend;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AddItemSteps {

    private WebDriver driver;
    private HomePage homePage;
    private BookPage bookPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private OrdersPage ordersPage;
    private WebDriverWait webDriverWait;


    @Before
    public void setup() {
        driver = WebDriverManager.firefoxdriver().create();
        homePage = new HomePage(driver);
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

    }

    @Given("I am logged in")
    public void doLogin(){
        homePage.clickSignIn();
        SignUpPage signUpPage = new SignUpPage(driver);
        signUpPage.fillEmail("client@gmail.com");
        signUpPage.fillPassword("client");
        signUpPage.submit();
    }

    @Given("I am in the home page")
    public void theUserIsOnTheHomepage() {
        webDriverWait.until(driver -> driver.getCurrentUrl().equals("http://localhost:5173/"));
    }

    @Given("I click on the first item")
    public void clickInTheFirstItem() {
        homePage.clickFirstBook();
    }

    @And("I click on the add to cart button")
    public void iClickOnTheAddToCartButton() {
        bookPage = new BookPage(driver);
        bookPage.addItem();
        bookPage.openCartButton();
        assertEquals("1 Items", bookPage.getTotalItem());
    }

    @Then("I should see the item in the cart")
    public void iShouldSeeTheItemInTheCart() {
        bookPage.viewCart();
        cartPage = new CartPage(driver);
        assertDoesNotThrow(()-> cartPage.getItem());
    }

    @And("I have items in my cart")
    public void iHaveItemsInMyCart() {
        webDriverWait.until(driver -> driver.getCurrentUrl().equals("http://localhost:5173/"));
        homePage.clickFirstBook();
        bookPage = new BookPage(driver);
        webDriverWait.until(driver -> driver.getCurrentUrl().equals("http://localhost:5173/product/29bf50ad-5b9e-4f0e-9fb8-710f0f4b0ed6"));
        bookPage.addItem();
        bookPage.openCartButton();
        assertEquals("1 Items", bookPage.getTotalItem());
    }

    @And("I am on the cart page")
    public void iAmOnTheCartPage() {
        bookPage.viewCart();
        cartPage = new CartPage(driver);
        assertDoesNotThrow(()-> cartPage.getItem());
    }

    @When("I click on the checkout button")
    public void iClickOnTheCheckoutButton() {
        cartPage.clickCheckoutButton();
    }

    @Then("I should see the checkout page")
    public void iShouldSeeTheCheckoutPage() {
        webDriverWait.until(driver -> driver.getCurrentUrl().equals("http://localhost:5173/payment"));
    }

    @And("I am filling the checkout form")
    public void iAmFillingTheCheckoutForm() {
        checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillCheckoutForm(1234567891234567L, "John Doe", "2024-05-10", 123);
    }

    @And("I select the pickup option")
    public void iSelectThePickupOption() {
        checkoutPage.selectPickup();
    }

    @And("I click on the submit button")
    public void iClickOnTheSubmitButton() {
        checkoutPage.submit();
    }

    @Then("I should see the confirmation the pagament was successful")
    public void iShouldSeeTheConfirmationThePagamentWasSuccessful() {
        webDriverWait.until(driver -> driver.getCurrentUrl().equals("http://localhost:5173/orders"));
    }

    @When("I click on orders")
    public void iClickOnOrders() {
        homePage.clickOrders();
    }

    @And("I should see my orders")
    public void shouldSeeMyOrders() {
        webDriverWait.until(driver -> driver.getCurrentUrl().equals("http://localhost:5173/orders"));
        ordersPage = new OrdersPage(driver);
        assertEquals("My Orders", ordersPage.getTitle());
    }

    @And("I should see the order status")
    public void iShouldSeeTheOrderStatus() {
        ordersPage.waitcardOrder();
        assertTrue(ordersPage.getStatus());
    }

    @And("I click on the remove button")
    public void iClickOnTheRemoveButton() {
        cartPage.clickRemoveButton();
    }

    @Then("I should not see the item in the cart")
    public void iShouldSeeTheItemRemovedFromTheCart() {
        assertEquals("0€", cartPage.getTotal());
    }
}
