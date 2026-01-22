package factory;

import java.time.Duration;
import java.util.Properties;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import utilities.ConfigReader;

public class DriverFactory {

    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();
    private static Properties prop;

    /**
     * Initializes a new WebDriver instance if not present or if previous session is closed.
     */
    public static WebDriver init_Driver() {
        try {
            if (!isDriverActive()) {
                // Load config
                ConfigReader configReader = new ConfigReader();
                prop = configReader.init_prop();

                String browser = prop.getProperty("browser", "chrome").toLowerCase();
                String zoom = prop.getProperty("zoom", "0.8"); // default 80%

                System.out.println("Browser value from config: " + browser);
                System.out.println("Zoom level from config: " + zoom);

                switch (browser) {
                    case "chrome":
                        WebDriverManager.chromedriver().setup();

                        ChromeOptions chromeOptions = new ChromeOptions();
                        chromeOptions.addArguments("--start-maximized");

                        tlDriver.set(new ChromeDriver(chromeOptions));
                        break;

                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        tlDriver.set(new FirefoxDriver());
                        tlDriver.get().manage().window().maximize();
                        break;

                    default:
                        throw new RuntimeException("Invalid browser in config.properties: " + browser);
                }

                WebDriver driver = getDriver();

                // Common setup
                driver.manage().deleteAllCookies();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
                driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15));

                // Apply page zoom using JavaScript
                try {
                    ((JavascriptExecutor) driver)
                            .executeScript("document.body.style.zoom='" + zoom + "'");
                    System.out.println("✅ Browser zoom set to " + (Double.parseDouble(zoom) * 100) + "%");
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to set zoom: " + e.getMessage());
                }
            }
        } catch (WebDriverException e) {
            System.err.println("WebDriverException while initializing driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create WebDriver session", e);
        } catch (Exception e) {
            System.err.println("Unexpected exception while initializing driver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create WebDriver session due to unexpected error", e);
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
                System.err.println("Error while quitting driver: " + e.getMessage());
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
