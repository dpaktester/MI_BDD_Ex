package factory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriverException;

import utilities.ConfigReader;

public class DriverFactory {

    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    private static Properties prop;

    /**
     * Initializes a new WebDriver instance if not present or if previous session is closed.
     */
    public static WebDriver init_Driver() {
        if (!isDriverActive()) { // ✅ Check if driver is usable
            ConfigReader configReader = new ConfigReader();
            prop = configReader.init_prop();

            String browser = prop.getProperty("browser", "chrome").toLowerCase();
            System.out.println("Browser value from config: " + browser);

            try {
                switch (browser) {
                    case "chrome":
                        WebDriverManager.chromedriver().setup();
                        tlDriver.set(new ChromeDriver());
                        break;
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        tlDriver.set(new FirefoxDriver());
                        break;
                    default:
                        throw new RuntimeException("Invalid browser in config.properties: " + browser);
                }

                getDriver().manage().deleteAllCookies();
                getDriver().manage().window().maximize();
                getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            } catch (WebDriverException e) {
                throw new RuntimeException("Failed to create WebDriver session for browser: " + browser, e);
            }
        }
        return getDriver();
    }

    /**
     * Returns the active WebDriver instance for the current thread.
     */
    public static synchronized WebDriver getDriver() {
        return tlDriver.get();
    }

    /**
     * Quits the browser and clears the ThreadLocal instance.
     */
    public static void quitDriver() {
        WebDriver driver = tlDriver.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error while quitting driver: " + e.getMessage());
            }
            tlDriver.remove();
        }
    }

    /**
     * Checks if the driver is still active (session not closed).
     */
    private static boolean isDriverActive() {
        WebDriver driver = tlDriver.get();
        if (driver == null) {
            return false;
        }
        try {
            driver.getTitle(); // ping the driver
            return true;
        } catch (WebDriverException e) {
            return false;
        }
    }
}
