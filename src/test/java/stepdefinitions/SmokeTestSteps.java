package stepdefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.testng.Assert;
import pages.LoginPage;
import factory.DriverFactory;

public class SmokeTestSteps {

    private LoginPage loginPage;
    private WebDriver driver;

    @Given("I open the browser")
    public void i_open_the_browser() {
        driver = DriverFactory.getDriver(); // ✅ No new driver, reuse from AppHooks
        loginPage = new LoginPage(driver);
    }

    @Given("I navigate to url")
    public void i_navigate_to_url() {
        loginPage.userEntersUrl(); // still works if needed separately
    }

    @Then("I should see the Playground section visible")
    public void i_should_see_the_playground_section_visible() throws Exception {
        loginPage.clickPlayGroundMenuBtn();
        WebElement playgroundSection = driver.findElement(By.xpath("//*[@id='Playground']/div/div/div[1]/div[1]/div/h3"));
        Assert.assertTrue(playgroundSection.isDisplayed(), "Playground section should be visible.");
    }

    @Then("The page title should contain {string}")
    public void the_page_title_should_contain(String expectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertTrue(actualTitle.contains(expectedTitle),
                "Expected page title to contain: " + expectedTitle + ", but was: " + actualTitle);
    }
    @Then("User clicks on playground button")
    public void user_clicks_on_playground_button() {
        loginPage.clickPlayGroundMenuBtn();
    }
    @Then("User enters {string} to text box")
    public void user_enters_to_text_box(String text) throws Exception {
        // Example locator - change according to your application's DOM
        loginPage.enterText("Deepak");

    }
    
}
