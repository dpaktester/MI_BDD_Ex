package pages;

import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import factory.DriverFactory;
import utilities.ConfigReader;
import utilities.ElementUtil;
import utilities.PasswordEncryption;

public class LoginPage {

    private WebDriver driver;
    private ElementUtil elementutil;
    private PasswordEncryption pwencrypt;
    private Properties prop;

    // Locators
    @FindBy(xpath = "//a[contains(@href, 'playground') and .//span[contains(text(), 'Playground')]]")
    private WebElement playgroundBtn;
    @FindBy(xpath="//*[@id='text-input']")
    private WebElement textinput;

    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;

        // Initialize utilities
        this.elementutil = new ElementUtil(driver);
        this.pwencrypt = new PasswordEncryption();
        // Load config properties
        ConfigReader configReader = new ConfigReader();
        this.prop = configReader.init_prop();

        // Waits + page factory init
        elementutil.implicitWait();
        PageFactory.initElements(driver, this);
    }

    // Actions
    public void userEntersUrl() {
        driver.get(prop.getProperty("url"));
    }

    public void clickPlayGroundMenuBtn() {
        elementutil.click(playgroundBtn);
    }
    public void enterText(String inputText)throws Exception {
    	elementutil.input(textinput,inputText);
    }
}
