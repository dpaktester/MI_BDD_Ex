package applicationhooks;

import java.util.Properties;

import io.cucumber.java.AfterStep;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import factory.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utilities.ConfigReader;

public class AppHooks {

    private WebDriver driver;
    private Properties prop;

    @Before(order = 0)
    public void loadConfig() {
        ConfigReader configReader = new ConfigReader();
        prop = configReader.init_prop();
    }

    @Before(order = 1)
    public void launchBrowserAndNavigate() {
        driver = DriverFactory.init_Driver();
        String url = prop.getProperty("url");
        if (url == null || url.trim().isEmpty()) {
            throw new RuntimeException("URL is missing in config.properties");
        }
        driver.get(url);

        try {
            ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='80%'");
            System.out.println("✅ Browser zoom set to 80%");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to set zoom: " + e.getMessage());
        }
    }

    @After(order = 0)
    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            String screenshotName = scenario.getName().replaceAll(" ", "_");
            byte[] sourcePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(sourcePath, "image/png", screenshotName);
        }
    }

    // Capture screenshot after every step
    @AfterStep
    public void addScreenshotAfterStep(Scenario scenario) {
        if (driver != null) {
            final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Step Screenshot");
        }
    }
}
