/*
 *
 *
 * @Author : Deepak Mahapatra
 *
 */

package utilities;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

//Utility class for various WebDriver interactions
public class ElementUtil {

	private WebDriver driver;
	Properties prop;
	private ConfigReader configReader;
	public static Select select;
	static String parentWindow;

	public static int ranNo;

	public ElementUtil(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Clicks on an element using JavaScript executor.
	 *
	 * @param element The WebElement to be clicked.
	 */
	public void javaScriptClick(WebElement element) {
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript("arguments[0].click();", element);
	}



	/**
	 * Clicks on a given web element.
	 *
	 * @param element The WebElement to be clicked.
	 */
	public void click(WebElement element) {
		try {
			element.click();
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clicks an element safely: waits until clickable,
	 * tries normal click, falls back to JS click if intercepted.
	 */
	public void safeClick(WebElement element) {
		int retries = 3;
		while (retries > 0) {
			try {
				// Wait until element is clickable
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
				wait.until(ExpectedConditions.elementToBeClickable(element));

				// Scroll element into view (centered)
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);

				// Attempt normal click
				element.click();
				System.out.println("Clicked successfully: " + element);
				return;

			} catch (ElementClickInterceptedException e) {
				System.out.println("Click intercepted. Retrying with JS click...");
				try {
					Thread.sleep(500); // short wait before JS click
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
					System.out.println("Clicked via JS successfully: " + element);
					return;
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("Thread interrupted during JS click retry", ex);
				}

			} catch (StaleElementReferenceException e) {
				System.out.println("Element went stale. Retrying...");
				retries--;
				try {
					Thread.sleep(500); // wait for DOM to stabilize
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("Thread interrupted during stale element retry", ex);
				}

			} catch (TimeoutException e) {
				throw new RuntimeException("Element was never clickable within timeout: " + element, e);

			} catch (Exception e) {
				throw new RuntimeException("safeClick failed: " + e.getMessage(), e);
			}
		}
		throw new RuntimeException("safeClick failed after retries: " + element);
	}
	/**
	 * Attempts to click on the element normally; if that fails,
	 * performs a JavaScript click as a fallback.
	 *
	 * @param element The WebElement to be clicked.
	 */
	public void javaScriptClickWithFallback(WebElement element) {
		try {
			element.click();
		} catch (WebDriverException e) {
			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
			jsExecutor.executeScript("arguments[0].click();", element);
			e.printStackTrace();
		}
	}
	/**
	 * Performs a right-click (context click) on the element located by the given
	 * locator.
	 *
	 * @param element The locator to find the WebElement.
	 */
	public void contextClick(WebElement element) {
		Actions actions = new Actions(driver);
		actions.contextClick(element).perform();
		System.out.println("Performed right-click on the given element.");
	}


	/**
	 * Performs a right-click (context click) on a web element and presses the ENTER key
	 * using the Robot class. This can be used to interact with context menus
	 * that appear after right-clicking on an element.
	 *
	 * @param locator - The By locator of the element to right-click on.
	 * @throws Exception if the element is not found or Robot actions fail.
	 */
	public void contextClickAndPressEnter(By locator) throws Exception {
		// Create Actions instance for mouse interactions
		Actions actions = new Actions(driver);

		// Wait for the element to be present and visible (optional but recommended)
		WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.visibilityOfElementLocated(locator));

		// Perform right-click on the element
		actions.contextClick(element).perform();

		// Use Robot class to simulate keyboard actions
		Robot robot = new Robot();

		// Small delay to allow context menu to appear
		robot.delay(500);

		// Navigate through context menu using DOWN and then ENTER
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyRelease(KeyEvent.VK_DOWN);
		robot.delay(200);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}


	/**
	 * Performs a double-click on the element located by the given locator.
	 *
	 * @param element The locator to find the WebElement.
	 */
	public void doubleClick(WebElement element) {
		// Create Actions instance for mouse interactions
		Actions actions = new Actions(driver);

		// Wait for the element to be clickable before performing the action
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.elementToBeClickable(element));

		// Perform double-click
		actions.doubleClick(element).perform();

		System.out.println("Double-clicked on element: " + element.toString());
	}


	/**
	 * Moves to the element, performs a double-click, then a single click.
	 *
	 * @param element The locator to find the WebElement.
	 */
	public void clickEvent(WebElement element) {
		// Create an Actions instance for mouse interactions
		Actions actions = new Actions(driver);

		// Wait until the element is clickable before performing the action
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.elementToBeClickable(element));

		// Move to element, double-click, then single-click — ensures action is registered
		actions.moveToElement(element).doubleClick().click().build().perform();
		System.out.println("Performed click event on element: " + element.toString());
	}

	/**
	 * Waits until the element located by the given locator is clickable,
	 * then clicks on it.
	 *
	 * @param element The locator to find the WebElement.
	 */
	public void waitUntilElementLoadsAndClick(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
		// Wait until element is clickable
		wait.until(ExpectedConditions.elementToBeClickable(element));
		// Perform click
		element.click();
		System.out.println("Clicked on element after waiting until it was clickable: " + element.toString());
	}

	/**
	 * Waits until the specified WebElement becomes visible on the page.
	 *
	 * @param webElement The {@link WebElement} to wait for.
	 */
	public void waitForElementToVisible(WebElement webElement) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(webElement));
	}

	/**
	 * Simulates pressing the UP arrow key five times using Robot.
	 *
	 * @throws Exception If Robot key press/release fails.
	 */
	public void clickUpArrowMultipleTimes() throws Exception {
		Robot robot = new Robot();
		for (int i = 0; i < 5; i++) {
			robot.keyPress(KeyEvent.VK_UP);
			robot.keyRelease(KeyEvent.VK_UP);
		}
	}

	/**
	 * Selects a dropdown option by visible text.
	 *
	 * @param dropdown    The dropdown WebElement.
	 * @param visibleText The visible text to select.
	 */
	public static void selectDropdownByText(WebElement dropdown, String visibleText) {
		Select select = new Select(dropdown);
		select.selectByVisibleText(visibleText);
	}

	/**
	 * Simulates pressing the F5 (refresh) key five times using Robot.
	 *
	 * @throws Exception If Robot key press/release fails.
	 */
	public void refreshPageMultipleTimes() throws Exception {
		Robot robot = new Robot();
		for (int i = 0; i < 5; i++) {
			robot.keyPress(KeyEvent.VK_F5);
			robot.keyRelease(KeyEvent.VK_F5);
		}
	}

	public void waitForPageRefreshComplete() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		try {
			// Wait until a key element becomes stale (page refresh in progress)
			wait.until(ExpectedConditions.stalenessOf(
					driver.findElement(By.cssSelector("selector-of-some-stable-element"))));

			// Wait until the same element reappears (page has refreshed)
			wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.cssSelector("selector-of-same-stable-element")));

		} catch (Exception e) {
			System.out.println("Page refresh wait timed out, proceeding anyway.");
		}
	}

	/**
	 * Clears the content of the field and inputs the specified value.
	 *
	 * @param element The WebElement representing the input field.
	 * @param value   The String value to input.
	 */
	public void clearAndInput(WebElement element, String value) {
		try {
			element.clear();
			element.sendKeys(value);
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inputs the specified value into the field without clearing it first.
	 *
	 * @param element The WebElement representing the input field.
	 * @param value   The String value to input.
	 */
	public void input(WebElement element, String value) {
		try {
			element.sendKeys(value);
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encrypts the password using Base64 encoding.
	 * (Note: The method currently returns the original password instead of encoded
	 * string.)
	 *
	 * @param password The password String to encrypt.
	 * @return The encrypted password as Base64 encoded string (currently returns
	 *         input).
	 */
	public String encryptPassword(String password) {
		configReader = new ConfigReader();
		prop = configReader.init_prop();
		byte[] encodedBytes = Base64.encodeBase64(prop.getProperty("password_personal").getBytes());
		return password; // Likely should return new String(encodedBytes)
	}

	/**
	 * Applies implicit wait for 30 seconds on the WebDriver.
	 */
	public void implicitWait(int waitTime) {

		//driver.manage().timeouts().implicitlyWait(30000, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitTime));

	}

	/**
	 * Waits explicitly for the element located by locator to be visible.
	 *
	 * @param locator The locator to find the element.
	 */

	public void explicitWait(WebElement locator , int WaitTime) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WaitTime));
		wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	/**
	 * Waits explicitly for the element located by locator to be clickable.
	 *
	 * @param element The WebElement
	 * @param timeoutSec The TimeOut in Seconds
	 */
	public void explicitWaitUntilClickable(WebElement element, int timeoutSec) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element));
			System.out.println("Element is clickable within " + timeoutSec + "s: " + element);
		} catch (TimeoutException e) {
			System.err.println("Timeout: Element not clickable within " + timeoutSec + "s → " + element);
			throw e;
		}
	}

	/**
	 * Waits until the DOM document readyState is "complete".
	 *
	 */
	public void waitForDomLoad(int waitTime) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
		try {
			wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
			System.out.println("DOM fully loaded.");
		} catch (TimeoutException e) {
			System.err.println("Timeout: Page did not load completely within 30s.");
			throw e;
		}
	}

	/**
	 * Waits for a WebElement to become clickable using FluentWait.
	 *
	 * @param element    WebElement to wait for.
	 * @param timeoutSec Maximum time to wait in seconds.
	 * @param pollingSec Polling interval in seconds to check the condition.
	 */
	public void fluentWaitForClickable(WebElement element, long timeoutSec, long pollingSec) {
		// Create a FluentWait instance with custom timeout and polling
		Wait<WebDriver> wait = new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(timeoutSec)) // Total wait time
				.pollingEvery(Duration.ofSeconds(pollingSec)) // Polling frequency
				.ignoring(NoSuchElementException.class) // Ignore element not found
				.ignoring(StaleElementReferenceException.class); // Ignore stale element issues

		// Wait until the element is clickable
		wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	// Generic reusable dropdown selector
	public void selectMatOption(WebElement dropdown, String optionText) {
		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(30));
		By spinnerOverlay = By.cssSelector("div.spinner-overlay");
		By snackbar = By.cssSelector("simple-snack-bar, mat-snack-bar-container");

		try {
			// 1. Wait for spinner overlay to disappear
			wait.until(ExpectedConditions.invisibilityOfElementLocated(spinnerOverlay));

			// 2. Wait for snackbar/toast if present
			try {
				wait.until(ExpectedConditions.invisibilityOfElementLocated(snackbar));
			} catch (TimeoutException ignored) {}

			// 3. Open dropdown
			wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

			// 4. Build option locator inside overlay panel
			By optionLocator = By.xpath(
					"//div[contains(@class,'cdk-overlay-pane')]//mat-option" +
							"//span[@class='mdc-list-item__primary-text' and normalize-space(text())='" + optionText + "']"
			);

			// 5. Wait for option to be visible
			WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));

			// 6. Scroll into center view
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({block: 'center'});", option);

			// 7. Retry normal click a few times
			boolean clicked = false;
			for (int attempts = 1; attempts <= 3 && !clicked; attempts++) {
				try {
					wait.until(ExpectedConditions.elementToBeClickable(option)).click();
					clicked = true;
					System.out.printf("Successfully selected: %s%n", optionText);
				} catch (ElementClickInterceptedException e) {
					System.out.printf("Attempt %d: Click intercepted, retrying for: %s%n", attempts, optionText);
					wait.withTimeout(Duration.ofMillis(500)); // short pause
				}
			}

			// 8. Fallback to JS click
			if (!clicked) {
				System.out.printf("Falling back to JS click for: %s%n", optionText);
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
				System.out.printf("Selected via JS click: %s%n", optionText);
			}

		} catch (TimeoutException te) {
			System.err.printf("Timeout: Could not find/select option '%s'%n", optionText);
			throw te;
		} catch (Exception e) {
			System.err.printf("Failed to select value: %s%n", optionText);
			throw new RuntimeException("Option selection failed: " + optionText, e);
		}
	}

	/**
	 * Selects a value from Angular Material <mat-select> dropdown by typing the
	 * first
	 * 3 characters and pressing ENTER. Works even if the option is deep in the
	 * list.
	 *
	 * @param dropdown   The <mat-select> WebElement
	 * @param optionText The full text of the option to select
	 */
	public void selectMatOptionByTyping(WebElement dropdown, String optionText) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		By panelLocator = By.cssSelector("div.mat-select-panel");

		try {
			// 1. Open dropdown
			wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

			// 2. Type first 3 letters
			String searchText = optionText.length() > 3 ? optionText.substring(0, 3) : optionText;
			WebElement inputBox = driver.switchTo().activeElement();
			inputBox.sendKeys(searchText);

			// 3. Wait until dropdown panel is visible
			wait.until(ExpectedConditions.visibilityOfElementLocated(panelLocator));

			// 4. Locate the option with full text and click it
			By optionLocator = By.xpath("//mat-option//span[normalize-space()='" + optionText + "']");
			WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
			option.click();

			// 5. Wait for panel to close
			wait.until(ExpectedConditions.invisibilityOfElementLocated(panelLocator));

			System.out.printf("liSelected option: %s%n", optionText);

		} catch (Exception e) {
			throw new RuntimeException("Could not select option: " + optionText, e);
		}
	}


	/**
	 * Selects an option from Angular Material MDC <mat-select> by typing the option
	 * text,
	 * scrolling it into view (if needed), and pressing ENTER.
	 *
	 * @param dropdown   The <mat-select> element
	 * @param optionText The option text to select
	 */
	// it handles the ElementClickInterceptedException
	public void selectMatOptionForAll(WebElement dropdown, String optionText) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		By spinnerOverlay = By.cssSelector("div.spinner-overlay");

		try {
			// 1. Wait for spinner/overlay to disappear
			wait.until(ExpectedConditions.invisibilityOfElementLocated(spinnerOverlay));

			// 2. Wait for dropdown clickable and click
			wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

			// 3. Build the dynamic locator for the option
			By optionLocator = By.xpath("//mat-option//span[normalize-space(.)='" + optionText + "']");

			// 4. Wait for option presence
			WebElement option = wait.until(ExpectedConditions.presenceOfElementLocated(optionLocator));

			// 5. Scroll into view
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({block: 'center'});", option);

			// 6. Try clicking
			try {
				wait.until(ExpectedConditions.elementToBeClickable(option)).click();
			} catch (ElementClickInterceptedException e) {
				System.out.println("Normal click intercepted, retrying with JS for option: " + optionText);
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
			}

			System.out.println("Successfully selected option: " + optionText);

		} catch (TimeoutException te) {
			System.out.println("Timeout: Option '" + optionText + "' not found/clickable within 10s.");
		} catch (Exception e) {
			System.out.println("Failed to select option: " + optionText);
			e.printStackTrace();
		}
	}
	/**
	 * Selects a dropdown option by index.
	 *
	 * @param dropdown The dropdown WebElement.
	 * @param index    The index of the option to select.
	 */
	public static void selectDropdownByIndex(WebElement dropdown, int index) {
		Select select = new Select(dropdown);
		select.selectByIndex(index);
	}

	/**
	 * Selects a dropdown option by value attribute.
	 *
	 * @param dropdown The dropdown WebElement.
	 * @param value    The value attribute of the option to select.
	 */
	public static void selectDropdownByValue(WebElement dropdown, String value) {
		Select select = new Select(dropdown);
		select.selectByValue(value);
	}

	/**
	 * Returns all option elements from a dropdown.
	 *
	 * @param dropdown The dropdown WebElement.
	 * @return List of WebElement options.
	 */
	public static List<WebElement> getDropdownOptions(WebElement dropdown) {
		select = new Select(dropdown);
		return select.getOptions();
	}

	/**
	 * Returns all dropdown option texts (non-empty values) as a list of Strings.
	 *
	 * @param dropdownElement The locator to find the dropdown element.
	 * @return List of option texts.
	 */
	public List<String> getDropdownOptionTexts(WebElement dropdownElement) {
		List<String> options = new ArrayList<>();
		Select select = new Select(dropdownElement);

		for (WebElement option : select.getOptions()) {
			String value = option.getAttribute("value");
			if (value != null && !value.trim().isEmpty()) {
				options.add(option.getText().trim());
			}
		}
		return options;
	}


	/**
	 * Switches to a new browser tab by clicking an element with the given ID,
	 * then switching to the second tab.
	 *
	 * @param id The ID of the element to click to open the new tab.
	 */
	public void switchToNewTabById(String id) {
		driver.findElement(By.id(id)).click();
		ArrayList<String> allTabs = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(allTabs.get(1));
	}

	/**
	 * Switches to the second browser tab.
	 *
	 */
	public void switchToNewTab() {
		parentWindow = driver.getWindowHandle();
		System.out.println("Parent Window Is :: " + parentWindow);
		Set<String> allWindows = driver.getWindowHandles();
		ArrayList<String> tabs = new ArrayList<>(allWindows);
		driver.switchTo().window(tabs.get(1));
	}

	/**
	 * Switches back to the parent browser tab.
	 */
	public void switchToParentTab() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.switchTo().window(parentWindow);
	}

	/**
	 * Scrolls the page down using the PAGE_DOWN key via Robot.
	 *
	 * @throws Exception If Robot key press/release fails.
	 */
	public void scrollDown() throws Exception {
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_PAGE_DOWN);
		robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
	}

	/**
	 * Scrolls to the bottom of the page using JavaScript.
	 */
	public void goToBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	/**
	 * Scrolls the given WebElement into view at the bottom of the viewport.
	 *
	 * @param element WebElement to scroll to
	 */
	public void scrollToElementBottom(WebElement element) {
		try {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView({block: 'end', inline: 'nearest'});", element);
		} catch (Exception e) {
			System.out.println("Failed to scroll element into view: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Scrolls the page up using the PAGE_UP key via Robot.
	 *
	 * @throws Exception If Robot key press/release fails.
	 */
	public void scrollUp() throws Exception {
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_PAGE_UP);
		robot.keyRelease(KeyEvent.VK_PAGE_UP);
	}

	/**
	 * Inputs the current date (formatted as MM/dd/YYYY) into a date field,
	 * deleting existing content and typing parts sequentially.
	 *
	 * @param element The date input WebElement.
	 * @throws Exception If thread sleep or Robot operations fail.
	 */
	public void enterStartDate(WebElement element) throws Exception {
		ElementUtil elementUtil = new ElementUtil(driver);
		LocalDate startDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY");
		String formattedDate = formatter.format(startDate);

		Thread.sleep(5000);
		elementUtil.javaScriptClick(element);
		Thread.sleep(5000);

		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_DELETE);
		robot.keyRelease(KeyEvent.VK_DELETE);

		for (String part : formattedDate.split("/")) {
			element.sendKeys(part);
		}
	}

	/**
	 * Inputs a future date (current date + 666 days) into a date field,
	 * deleting existing content and typing parts sequentially.
	 *
	 * @param element The date input WebElement.
	 */
	public void enterEndDate(WebElement element) {
		LocalDate endDate = LocalDate.now().plusDays(666);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YYYY");
		String formattedDate = formatter.format(endDate);

		element.sendKeys(Keys.DELETE);
		element.sendKeys(Keys.DELETE);

		for (String part : formattedDate.split("/")) {
			element.sendKeys(part);
		}
	}

	/**
	 * Generates a random integer between 1 and 50 and stores it in ranNo.
	 */
	public void generateRandomNumber() {
		Random random = new Random();
		ranNo = random.nextInt(50) + 1;
	}

	/**
	 * Generic method to select multiple options from a dropdown.
	 *
	 * @param dropdownElement WebElement of the select tag
	 * @param selectType      "visibleText", "value", or "index"
	 * @param values          List of strings or indexes (as strings for index)
	 */
	public static void selectMultiple(WebElement dropdownElement, String selectType, List<String> values) {
		Select select = new Select(dropdownElement);

		if (!select.isMultiple()) {
			throw new IllegalArgumentException("Dropdown does not support multiple selections.");
		}

		for (String value : values) {
			try {
				switch (selectType.toLowerCase()) {
					case "visibletext":
						select.selectByVisibleText(value);
						break;
					case "value":
						select.selectByValue(value);
						break;
					case "index":
						select.selectByIndex(Integer.parseInt(value));
						break;
					default:
						throw new IllegalArgumentException("Invalid selection type: " + selectType);
				}
				System.out.println("Selected: " + value);
			} catch (Exception e) {
				System.out.println("Unable to select: " + value + " - " + e.getMessage());
			}
		}
	}

	/**
	 * Generic method to handle searchable dropdowns.
	 *
	 * @param dropdownOpener Locator for the element that opens the dropdown
	 * @param searchBox      Locator for the search input box inside the dropdown
	 * @param resultsList    Locator for the dropdown result items
	 * @param valuesToSelect List of values to search and select
	 */
	public void selectFromSearchableDropdown(By dropdownOpener,
													By searchBox,
													By resultsList,
													List<String> valuesToSelect) {
		WebElement opener = driver.findElement(dropdownOpener);

		for (String value : valuesToSelect) {
			try {
				// Open the dropdown
				opener.click();

				// Type in the search box
				WebElement searchInput = driver.findElement(searchBox);
				searchInput.clear();
				searchInput.sendKeys(value);
				searchInput.sendKeys(Keys.ENTER); // some dropdowns auto-select on Enter

				// If dropdown shows results, click the matching one
				List<WebElement> results = driver.findElements(resultsList);
				for (WebElement result : results) {
					if (result.getText().trim().equalsIgnoreCase(value)) {
						result.click();
						break;
					}
				}
				System.out.println("Selected from searchable dropdown: " + value);
			} catch (Exception e) {
				System.out.println("Unable to select: " + value + " - " + e.getMessage());
			}
		}
	}

	/**
	 * Selects one or more checkboxes from a group based on their visible text or
	 * value attribute.
	 *
	 * @param groupElement   The common locator for all checkboxes in the group
	 * @param valuesToSelect The array of values/texts of the checkboxes to be selected
	 */
	public void selectCheckboxes(WebElement groupElement, String[] valuesToSelect) {
		// Find all checkboxes inside the provided group element
		List<WebElement> checkboxes = groupElement.findElements(By.xpath(".//input[@type='checkbox']"));

		// Loop through each value we want to select
		for (int i = 0; i < valuesToSelect.length; i++) {
			String valueToSelect = valuesToSelect[i];

			// Loop through each checkbox in the group
			for (int j = 0; j < checkboxes.size(); j++) {
				WebElement checkbox = checkboxes.get(j);
				String checkboxText = checkbox.getText().trim();
				String checkboxValue = checkbox.getAttribute("value");

				// Check if text or value matches, and if not already selected
				if ((checkboxText.equalsIgnoreCase(valueToSelect) || checkboxValue.equalsIgnoreCase(valueToSelect))
						&& !checkbox.isSelected()) {
					checkbox.click();
					break; // move to next value once matched and clicked
				}
			}
		}
	}



	/**
	 * Sets a toggle switch to the desired state (ON/OFF)
	 *
	 * @param toggleLocator Locator for the toggle element
	 * @param turnOn        true to turn ON, false to turn OFF
	 */
	/**
	 * Toggles a switch (like ON/OFF button or checkbox) based on desired state.
	 *
	 * @param toggleElement The toggle WebElement.
	 * @param turnOn        True to turn ON, False to turn OFF.
	 */
	public void setToggle(WebElement toggleElement, boolean turnOn) {
		boolean isOn;

		// Check if it's an input type="checkbox"
		if (toggleElement.getTagName().equalsIgnoreCase("input") &&
				"checkbox".equalsIgnoreCase(toggleElement.getAttribute("type"))) {
			isOn = toggleElement.isSelected();
		} else {
			// Fallback: determine ON/OFF by class or aria attributes
			String classAttr = toggleElement.getAttribute("class");
			String ariaChecked = toggleElement.getAttribute("aria-checked");

			isOn = (ariaChecked != null && ariaChecked.equalsIgnoreCase("true")) ||
					(classAttr != null && classAttr.toLowerCase().contains("on"));
		}

		// Click only if state needs to be changed
		if (turnOn && !isOn) {
			toggleElement.click();
		} else if (!turnOn && isOn) {
			toggleElement.click();
		}
	}

	/**
	 * Moves a slider to a specific value and prints the new value after movement.
	 *
	 * @param sliderElement The slider WebElement.
	 * @param targetValue   The desired slider value.
	 */
	public void setSliderValue(WebElement sliderElement, int targetValue) {
		Actions actions = new Actions(driver);

		String minAttr = sliderElement.getAttribute("min");
		String maxAttr = sliderElement.getAttribute("max");

		if (minAttr != null && maxAttr != null) {
			int min = Integer.parseInt(minAttr);
			int max = Integer.parseInt(maxAttr);
			int currentValue = Integer.parseInt(sliderElement.getAttribute("value"));

			int sliderWidth = sliderElement.getSize().width;
			double valuePerPixel = (double) (max - min) / sliderWidth;

			int moveOffset = (int) ((targetValue - currentValue) / valuePerPixel);

			actions.clickAndHold(sliderElement).moveByOffset(moveOffset, 0).release().perform();
		} else {
			// Fallback for custom sliders
			actions.dragAndDropBy(sliderElement, targetValue, 0).perform();
		}

		// Display new slider value
		String newValue = sliderElement.getAttribute("value");
		if (newValue != null) {
			System.out.println("Slider value after sliding: " + newValue);
		} else {
			System.out.println("Slider moved, but value not available in attribute.");
		}
	}

	/**
	 * Drags a source element and drops it onto a target element.
	 *
	 * @param sourceElement Locator for the element to drag
	 * @param targetElement Locator for the element to drop onto
	 */
	public void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
		Actions actions = new Actions(driver);
		actions.dragAndDrop(sourceElement, targetElement).perform();

		System.out.println("Dragged element from source to target successfully.");
	}


	/**
	 * Uploads a file by sending the file path to an input[type='file'] element.
	 *
	 * @param fileInputElement Locator for the file input element
	 * @param filePath         Absolute path of the file to upload
	 */
	public void uploadFile(WebElement fileInputElement, String filePath) {
		fileInputElement.sendKeys(filePath);
		System.out.println("File uploaded: " + filePath);
	}

	public static boolean uploadFile1(WebElement fileInput, String filePath) {
		try {
			fileInput.sendKeys(filePath);
			System.out.println("File uploaded: " + filePath);
			return true;
		} catch (Exception e) {
			System.out.println("File upload failed: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Generic method to upload a file into <input type="file">.
	 *
	 * @param locatorOrElement Either By locator or WebElement of file input
	 * @param filePath         Absolute file path to upload
	 * @return true if upload succeeded, false otherwise
	 */
	public boolean uploadFileGeneric(Object locatorOrElement, String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				System.err.println("File not found: " + filePath);
				return false;
			}

			WebElement fileInput;

			if (locatorOrElement instanceof By) {
				fileInput = driver.findElement((By) locatorOrElement);
			} else if (locatorOrElement instanceof WebElement) {
				fileInput = (WebElement) locatorOrElement;
			} else {
				throw new IllegalArgumentException("locatorOrElement must be By or WebElement");
			}

			fileInput.sendKeys(file.getAbsolutePath());
			System.out.println("File uploaded: " + file.getAbsolutePath());
			return true;

		} catch (Exception e) {
			System.err.println("File upload failed: " + e.getMessage());
			return false;
		}
	}

	public boolean uploadFilesFromFolder(By fileInputLocator, String folderPath) {
		File folder = new File(folderPath);

		if (!folder.exists() || !folder.isDirectory()) {
			System.out.println("Invalid folder path: " + folderPath);
			return false;
		}

		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			System.out.println("No files found in folder: " + folderPath);
			return false;
		}

		try {
			for (File file : files) {
				if (file.isFile()) {
					WebElement fileInput = driver.findElement(fileInputLocator);
					fileInput.sendKeys(file.getAbsolutePath());
					System.out.println("Uploaded file: " + file.getName());
					// You can add wait/assert here if UI updates after upload
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Uploads a folder using an <input type="file" webkitdirectory> element.
	 *
	 * @param folderInputLocator Locator for the folder input element
	 * @param folderPath         Absolute path of the folder to upload
	 */
	public void uploadFolder(By folderInputLocator, String folderPath) {
		WebElement folderInput = driver.findElement(folderInputLocator);
		folderInput.sendKeys(folderPath);

		System.out.println("Folder uploaded: " + folderPath);
	}

	/**
	 * Clicks a download link or button to download a file.
	 *
	 * @param downloadLocator Locator for the download link/button
	 */
	public void downloadFile(By downloadLocator) {
		WebElement downloadButton = driver.findElement(downloadLocator);
		downloadButton.click();
		System.out.println("Download initiated for: " + downloadLocator);
	}

	/**
	 * Switches to an iframe by index, name/id, or locator.
	 *
	 * @param frameIdentifier Can be Integer (index), String (name/id), or By
	 *                        (locator)
	 */
	public void switchToFrame(Object frameIdentifier) {
		try {
			if (frameIdentifier instanceof Integer) {
				driver.switchTo().frame((Integer) frameIdentifier);
				System.out.println("Switched to iframe by index: " + frameIdentifier);
			} else if (frameIdentifier instanceof String) {
				driver.switchTo().frame((String) frameIdentifier);
				System.out.println("Switched to iframe by name/id: " + frameIdentifier);
			} else if (frameIdentifier instanceof By) {
				WebElement frameElement = driver.findElement((By) frameIdentifier);
				driver.switchTo().frame(frameElement);
				System.out.println("Switched to iframe by locator: " + frameIdentifier);
			} else {
				throw new IllegalArgumentException(
						"Invalid frameIdentifier type. Use index (int), name/id (String), or locator (By).");
			}
		} catch (Exception e) {
			System.err.println("Error switching to iframe: " + e.getMessage());
		}
	}

	/**
	 * Switches back to the main page content from any iframe.
	 */
	public void switchToDefault() {
		driver.switchTo().defaultContent();
		System.out.println("Switched back to default content.");
	}

	/**
	 * Searches a dynamic table for a given text and clicks it if found.
	 *
	 * @param tableElement Locator for the table element
	 * @param searchText   The text to search for in the table
	 * @return true if found and clicked, false otherwise
	 */
	public boolean searchAndClickInTable(WebElement tableElement, String searchText) {
		try {
			// Get all table rows
			List<WebElement> rows = tableElement.findElements(By.tagName("tr"));

			for (int i = 0; i < rows.size(); i++) {
				List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));

				for (WebElement cell : cells) {
					String cellText = cell.getText().trim();

					if (cellText.equalsIgnoreCase(searchText)) {
						cell.click();
						System.out.println("Found and clicked: '" + searchText + "' in row " + (i + 1));
						return true;
					}
				}
			}

			System.out.println("Text not found in table: " + searchText);
			return false;

		} catch (StaleElementReferenceException e) {
			System.err.println("Table became stale while searching for: " + searchText);
			return false;

		} catch (Exception e) {
			System.err.println("Error while searching and clicking in table: " + e.getMessage());
			return false;
		}
	}
	/**
	 * Collapses an accordion section by clicking the same header again.
	 *
	 * @param accordionHeaderElement Locator for all accordion headers
	 * @param headerText              The visible text of the accordion section to
	 *                                collapse
	 * @return true if found and clicked, false otherwise
	 */
	public boolean expandAccordion(WebElement accordionHeaderElement, String headerText) {
		try {
			String actualHeaderText = accordionHeaderElement.getText().trim();

			if (actualHeaderText.equalsIgnoreCase(headerText)) {
				accordionHeaderElement.click();
				System.out.println("Accordion expanded: " + headerText);
				return true;
			} else {
				System.out.println("Accordion header text did not match: " + actualHeaderText);
				return false;
			}

		} catch (Exception e) {
			System.err.println("Failed to expand accordion: " + e.getMessage());
			return false;
		}
	}

	public boolean collapseAccordion(WebElement accordionHeaderElement, String headerText) {
		// Reuse the same logic as expandAccordion
		return expandAccordion(accordionHeaderElement, headerText);
	}


	/**
	 * Waits for a toast to appear, captures its text, and waits until it
	 * disappears.
	 *
	 * @param toastElement Locator for the toast element
	 * @param timeoutSec   Max wait time for toast appearance and disappearance
	 * @return The toast text
	 */
	public String captureToast(WebElement toastElement, int timeoutSec) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

		// Wait until the toast is visible
		wait.until(ExpectedConditions.visibilityOf(toastElement));

		String toastText = toastElement.getText().trim();
		System.out.println("Toast appeared: " + toastText);

		// Wait until the toast becomes invisible (disappears)
		wait.until(ExpectedConditions.invisibilityOf(toastElement));
		System.out.println("Toast disappeared.");

		return toastText;
	}
	/**
	 * Waits for an alert, handles it (accept or dismiss), and returns its text.
	 *
	 * @param timeoutSec Max wait time for alert (seconds)
	 * @param accept     true to accept alert, false to dismiss
	 * @return The alert text message
	 */
	public String handleAlert(int timeoutSec, boolean accept) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

		// Wait until the alert is present
		Alert alert = wait.until(ExpectedConditions.alertIsPresent());

		// Capture alert text
		String alertText = alert.getText();

		// Accept or dismiss the alert based on the flag
		if (accept) {
			alert.accept();
			System.out.println("Alert accepted.");
		} else {
			alert.dismiss();
			System.out.println("Alert dismissed.");
		}

		System.out.println("Alert text: " + alertText);
		return alertText;
	}

	/**
	 * Waits for a confirm alert, accepts or dismisses it, and returns the alert
	 * text.
	 *
	 * @param timeoutSec Max wait time for alert (seconds)
	 * @param accept     true to click OK (accept), false to click Cancel (dismiss)
	 * @return The alert text
	 */
	public String handleConfirmAlert(int timeoutSec, boolean accept) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

		// Wait for alert to appear
		Alert alert = wait.until(ExpectedConditions.alertIsPresent());

		// Capture alert text
		String alertText = alert.getText();

		// Accept or dismiss based on flag
		if (accept) {
			alert.accept();
			System.out.println("Confirm alert accepted.");
		} else {
			alert.dismiss();
			System.out.println("Confirm alert dismissed.");
		}

		System.out.println("Confirm alert text: " + alertText);
		return alertText;
	}

	/**
	 * Handles a prompt alert by sending input text, accepting or dismissing it, and
	 * returning the alert text.
	 *
	 * @param timeoutSec Max wait time for the alert (seconds)
	 * @param inputText  The text to send to the prompt input box
	 * @param accept     true to accept the prompt, false to dismiss
	 * @return The alert text before input was sent
	 */
	public String handlePromptAlert(int timeoutSec, String inputText, boolean accept) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

		// Wait for alert to be present
		Alert alert = wait.until(ExpectedConditions.alertIsPresent());

		// Capture alert text
		String alertText = alert.getText();

		// Send input if provided
		if (inputText != null && !inputText.isEmpty()) {
			alert.sendKeys(inputText);
			System.out.println("Input sent to prompt: " + inputText);
		}

		// Accept or dismiss the prompt
		if (accept) {
			alert.accept();
			System.out.println("Prompt alert accepted.");
		} else {
			alert.dismiss();
			System.out.println("Prompt alert dismissed.");
		}

		System.out.println("Prompt alert text: " + alertText);
		return alertText;
	}

	/**
	 * Clicks on a download link/button and verifies the file is downloaded
	 *
	 * @param downloadButton     Locator of the download element
	 * @param downloadDir Path of the download directory
	 * @param fileName    Expected file name
	 * @param timeoutSec  Max wait time in seconds
	 * @return true if file downloaded, false otherwise
	 */
	public boolean downloadFile(WebElement downloadButton, String downloadDir, String fileName, int timeoutSec) {
		try {
			// Click on the download button
			downloadButton.click();

			// Wait for file to appear
			File file = new File(downloadDir + File.separator + fileName);
			int waited = 0;

			while (waited < timeoutSec) {
				if (file.exists()) {
					System.out.println("File downloaded successfully: " + file.getAbsolutePath());
					return true;
				}
				Thread.sleep(1000);
				waited++;
			}

			System.err.println("File not downloaded within timeout: " + fileName);
			return false;

		} catch (Exception e) {
			System.err.println("File download failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Captures screenshot and saves with timestamp in given folder
	 *
	 * @param fileName Desired file name (without extension)
	 * @return The absolute path of the saved screenshot
	 */
	public String captureScreenshot(String fileName) {
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String screenshotName = fileName + "_" + timestamp + ".png";

		String destDir = System.getProperty("user.dir") + "/screenshots/";
		File dir = new File(destDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File destFile = new File(destDir + screenshotName);

		try {
			FileUtils.copyFile(srcFile, destFile);
			System.out.println("Screenshot saved at: " + destFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return destFile.getAbsolutePath();
	}

	/**
	 * Clicks a Material/Angular switch button safely,
	 * but only if the button is enabled.
	 *
	 * @param element The WebElement representing the switch button
	 */
	public void clickSwitch(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		try {
			// Wait until visible and clickable
			wait.until(ExpectedConditions.elementToBeClickable(element));

			if (element.isEnabled()) {
				// Scroll into view
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);

				try {
					element.click();
					System.out.println("Switch clicked successfully.");
				} catch (ElementClickInterceptedException e) {
					System.out.println("Normal click intercepted. Retrying with JS click...");
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
				}
			} else {
				System.out.println("Switch is disabled, skipping click.");
			}
		} catch (TimeoutException te) {
			System.out.println("Click failed: Switch not clickable within timeout.");
		} catch (Exception e) {
			System.out.println("Click on switch failed: " + e.getMessage());
		}
	}

	//
	/**
	 * Perform a simple mouse hover over an element.
	 *
	 * @param webElement The target WebElement to hover over.
	 */
	public void hoverOverElement(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).build().perform();
	}

	/**
	 * Perform a mouse hover over an element and click it.
	 *
	 * @param webElement Target WebElement to hover and click.
	 */
	public void hoverAndClick(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).click().build().perform();
	}

	/**
	 * Perform a mouse hover over a parent element, then move to a child element and
	 * click it.
	 * Useful for multi-level dropdown menus.
	 *
	 * @param parentMenu The parent menu WebElement to hover on.
	 * @param childMenu  The child menu WebElement to click.
	 */
	public void hoverParentAndClickChild(WebElement parentMenu, WebElement childMenu) {
		Actions act = new Actions(driver);
		act.moveToElement(parentMenu).moveToElement(childMenu).click().build().perform();
	}

	/**
	 * Perform a hover at a specific offset within an element.
	 *
	 * @param webElement The target element to hover on.
	 * @param xOffset    Horizontal offset in pixels relative to the element’s
	 *                   top-left corner.
	 * @param yOffset    Vertical offset in pixels relative to the element’s
	 *                   top-left corner.
	 */
	public void hoverWithOffset(WebElement webElement, int xOffset, int yOffset) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement, xOffset, yOffset).build().perform();
	}

	/**
	 * Perform a hover followed by a context-click (right-click) on the element.
	 *
	 * @param webElement Target WebElement for hover and right-click.
	 */
	public void hoverAndRightClick(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).contextClick().build().perform();
	}

	/**
	 * Perform a hover and double-click action on the element.
	 *
	 * @param webElement Target WebElement for hover and double-click.
	 */
	public void hoverAndDoubleClick(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).doubleClick().build().perform();
	}

	/**
	 * Perform a hover, then click and hold on the element (useful for drag-and-drop
	 * scenarios).
	 *
	 * @param webElement Target WebElement for hover and click-hold.
	 */
	public void hoverAndClickHold(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).clickAndHold().build().perform();
	}

	/**
	 * Perform a hover, click-and-hold on the element, then release (simulating a
	 * press and release).
	 *
	 * @param webElement Target WebElement for hover, click-hold, and release.
	 */
	public void hoverClickHoldAndRelease(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).clickAndHold().release().build().perform();
	}

	/**
	 * Perform a hover with a small pause (useful when menus take time to load on
	 * hover).
	 *
	 * @param webElement   Target WebElement to hover on.
	 * @param milliseconds Duration to pause in milliseconds.
	 * @throws InterruptedException if thread sleep is interrupted.
	 */
	public void hoverWithPause(WebElement webElement, int milliseconds)
			throws InterruptedException {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).build().perform();
		Thread.sleep(milliseconds);
	}

	/**
	 * Perform a hover and send keys to the hovered element.
	 *
	 * @param webElement Target WebElement to hover on.
	 * @param keys       Keys or string input to send.
	 */
	public void hoverAndSendKeys(WebElement webElement, CharSequence keys) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).sendKeys(keys).build().perform();
	}

	/**
	 * Hover over an element and return its visible text.
	 *
	 * @param webElement The WebElement to hover over.
	 * @return The text of the hovered element.
	 */
	public String hoverAndGetText(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).build().perform();
		return webElement.getText();
	}

	/**
	 * Hover over an element at a specific offset and return its text.
	 * Useful for elements where the hover area is not at the center.
	 *
	 * @param webElement Target WebElement.
	 * @param xOffset    Horizontal offset (pixels from left).
	 * @param yOffset    Vertical offset (pixels from top).
	 * @return The text of the hovered element.
	 */
	public String hoverWithOffsetAndGetText(WebElement webElement, int xOffset, int yOffset) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement, xOffset, yOffset).build().perform();
		return webElement.getText();
	}

	/**
	 * Hover over a parent menu, then a child menu, and return the child's text.
	 * Useful for multi-level dropdowns where hovering the parent reveals the child.
	 *
	 * @param parentMenu Parent menu WebElement to hover.
	 * @param childMenu  Child menu WebElement to hover and read text from.
	 * @return The text of the child menu element.
	 */
	public String hoverParentAndGetChildText(WebElement parentMenu, WebElement childMenu) {
		Actions act = new Actions(driver);
		act.moveToElement(parentMenu).moveToElement(childMenu).build().perform();
		return childMenu.getText();
	}

	/**
	 * Hover over an element, wait for a specified time, then return its text.
	 * Helpful when the text is revealed only after a delay.
	 *
	 * @param webElement   Target WebElement.
	 * @param milliseconds Duration to wait after hovering (in ms).
	 * @return The text of the hovered element.
	 * @throws InterruptedException if thread sleep is interrupted.
	 */
	public String hoverPauseAndGetText(WebElement webElement, int milliseconds)
			throws InterruptedException {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).build().perform();
		Thread.sleep(milliseconds);
		return webElement.getText();
	}

	/**
	 * Hover over an element, then send keys, and return its updated text.
	 * This is useful if sending keys triggers dynamic changes to the element’s
	 * text.
	 *
	 * @param webElement WebElement to hover on and send keys to.
	 * @param keys       Keys or string input to send.
	 * @return The text of the hovered element after sending keys.
	 */
	public String hoverSendKeysAndGetText(WebElement webElement, CharSequence keys) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).sendKeys(keys).build().perform();
		return webElement.getText();
	}

	/**
	 * Hover over an element, click and hold briefly, then return its text.
	 * Useful for tooltips or menu items that require a click-and-hold to show text.
	 *
	 * @param webElement WebElement to hover and click-hold.
	 * @return The text of the hovered element.
	 */
	public String hoverClickHoldAndGetText(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).clickAndHold().pause(500).release().build().perform();
		return webElement.getText();
	}

	/**
	 * Hover over an element, perform a double click, and return its text.
	 *
	 * @param webElement WebElement to hover and double-click.
	 * @return The text of the hovered element.
	 */
	public String hoverDoubleClickAndGetText(WebElement webElement) {
		Actions act = new Actions(driver);
		act.moveToElement(webElement).doubleClick().build().perform();
		return webElement.getText();
	}
}
