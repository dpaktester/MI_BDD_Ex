/*
 *
 *
 * @Author : Deepak Mahapatra
 *
 */


package utilities;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

//Utility class for various WebDriver interactions
public class ElementUtil {
	private WebDriver driver;
	Properties prop;
	private ConfigReader configReader;
	public static Select select;
	static String parentWindow ;
	public static int ranNo;
	  public ElementUtil(WebDriver driver) {
	        this.driver = driver;
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
	   * Attempts to click on the element normally; if that fails,
	   * performs a JavaScript click as a fallback.
	   *
	   * @param driver  The WebDriver instance.
	   * @param element The WebElement to be clicked.
	   */
	  public void javaScriptClickWithFallback(WebDriver driver, WebElement element) {
	      try {
	          element.click();
	      } catch (WebDriverException e) {
	          JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
	          jsExecutor.executeScript("arguments[0].click();", element);
	          e.printStackTrace();
	      }
	  }

	  /**
	   * Clicks on an element using JavaScript executor.
	   *
	   * @param driver  The WebDriver instance.
	   * @param element The WebElement to be clicked.
	   */
	  public void javaScriptClick(WebDriver driver, WebElement element) {
	      JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
	      jsExecutor.executeScript("arguments[0].click();", element);
	  }

	  /**
	   * Performs a right-click (context click) on the element located by the given locator.
	   *
	   * @param driver  The WebDriver instance.
	   * @param locator The locator to find the WebElement.
	   */
	  public void contextClick(WebDriver driver, By locator) {
	      Actions actions = new Actions(driver);
	      WebElement element = driver.findElement(locator);
	      actions.contextClick(element).perform();
	  }

	  /**
	   * Performs a context click on the element and then simulates pressing
	   * the DOWN arrow and ENTER keys via Robot.
	   *
	   * @param driver  The WebDriver instance.
	   * @param locator The locator to find the WebElement.
	   * @throws Exception If Robot key press/release fails.
	   */
	  public void contextClickAndPressEnter(WebDriver driver, By locator) throws Exception {
	      Actions actions = new Actions(driver);
	      WebElement element = driver.findElement(locator);
	      actions.contextClick(element).perform();

	      Robot robot = new Robot();
	      robot.keyPress(KeyEvent.VK_DOWN);
	      robot.keyRelease(KeyEvent.VK_DOWN);
	      robot.keyPress(KeyEvent.VK_ENTER);
	      robot.keyRelease(KeyEvent.VK_ENTER);
	  }

	  /**
	   * Performs a double-click on the element located by the given locator.
	   *
	   * @param driver  The WebDriver instance.
	   * @param locator The locator to find the WebElement.
	   */
	  public void doubleClick(WebDriver driver, By locator) {
	      Actions actions = new Actions(driver);
	      WebElement element = driver.findElement(locator);
	      actions.doubleClick(element).perform();
	  }

	  /**
	   * Moves to the element, performs a double-click, then a single click.
	   *
	   * @param driver  The WebDriver instance.
	   * @param locator The locator to find the WebElement.
	   */
	  public void clickEvent(WebDriver driver, By locator) {
	      Actions actions = new Actions(driver);
	      WebElement element = driver.findElement(locator);
	      actions.moveToElement(element).doubleClick().click().build().perform();
	  }

	  /**
	   * Waits until the element located by the given locator is clickable,
	   * then clicks on it.
	   *
	   * @param locator The locator to find the WebElement.
	   */
	  public void waitUntilElementLoadsAndClick(By locator) {
	      WebDriverWait wait = new WebDriverWait(driver, 10);
	      wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
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
	   * (Note: The method currently returns the original password instead of encoded string.)
	   *
	   * @param password The password String to encrypt.
	   * @return The encrypted password as Base64 encoded string (currently returns input).
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
	  public void implicitWait() {
	      driver.manage().timeouts().implicitlyWait(30000, TimeUnit.MILLISECONDS);
	  }

	  /**
	   * Waits explicitly for the element located by locator to be visible.
	   *
	   * @param locator The locator to find the element.
	   * @param driver  The WebDriver instance.
	   */
	  public void explicitWait(By locator, WebDriver driver) {
	      WebDriverWait wait = new WebDriverWait(driver, 30);
	      wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	  }

	  /**
	   * Waits explicitly for the element located by locator to be clickable.
	   *
	   * @param locator The locator to find the element.
	   * @param driver  The WebDriver instance.
	   */
	  public void explicitWaitUntilClickable(By locator, WebDriver driver) {
	      WebDriverWait wait = new WebDriverWait(driver, 30);
	      wait.until(ExpectedConditions.elementToBeClickable(locator));
	  }

	  /**
	   * Waits until the DOM document readyState is "complete".
	   *
	   * @param driver The WebDriver instance.
	   */
	  public void waitForDomLoad(WebDriver driver) {
	      WebDriverWait wait = new WebDriverWait(driver, 30);
	      wait.until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));
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
	   * @param locator The locator to find the dropdown element.
	   * @param driver  The WebDriver instance.
	   * @return List of option texts.
	   */
	  public static List<String> getDropdownOptionTexts(By locator, WebDriver driver) {
	      List<String> options = new ArrayList<>();
	      for (WebElement option : new Select(driver.findElement(locator)).getOptions()) {
	          if (!option.getAttribute("value").isEmpty()) {
	              options.add(option.getText());
	          }
	      }
	      return options;
	  }

	  /**
	   * Switches to a new browser tab by clicking an element with the given ID,
	   * then switching to the second tab.
	   *
	   * @param driver The WebDriver instance.
	   * @param id     The ID of the element to click to open the new tab.
	   */
	  public static void switchToNewTabById(WebDriver driver, String id) {
	      driver.findElement(By.id(id)).click();
	      ArrayList<String> allTabs = new ArrayList<>(driver.getWindowHandles());
	      driver.switchTo().window(allTabs.get(1));
	  }

	  /**
	   * Switches to the second browser tab.
	   *
	   * @param driver The WebDriver instance.
	   */
	  public static void switchToNewTab(WebDriver driver) {
	      parentWindow = driver.getWindowHandle();
	      System.out.println("Parent Window Is :: " + parentWindow);
	      Set<String> allWindows = driver.getWindowHandles();
	      ArrayList<String> tabs = new ArrayList<>(allWindows);
	      driver.switchTo().window(tabs.get(1));
	  }

	  /**
	   * Switches back to the parent browser tab.
	   *
	   * @param driver The WebDriver instance.
	   */
	  public static void switchToParentTab(WebDriver driver) {
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
	      elementUtil.javaScriptClick(driver, element);
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
	         * @param driver         WebDriver instance
	         * @param dropdownOpener Locator for the element that opens the dropdown
	         * @param searchBox      Locator for the search input box inside the dropdown
	         * @param resultsList    Locator for the dropdown result items
	         * @param valuesToSelect List of values to search and select
	         */
	        public static void selectFromSearchableDropdown(WebDriver driver,
	                                                        By dropdownOpener,
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
	         * Selects one or more checkboxes from a group based on their visible text or value attribute.
	         *
	         * @param driver The WebDriver instance
	         * @param groupLocator The common locator for all checkboxes in the group
	         * @param valuesToSelect The values/texts of the checkboxes to be selected
	         */
	        public static void selectCheckboxes(WebDriver driver, By groupLocator, String... valuesToSelect) {
	            List<WebElement> checkboxes = driver.findElements(groupLocator);

	            for (String value : valuesToSelect) {
	                for (WebElement checkbox : checkboxes) {
	                    String checkboxText = checkbox.getText().trim();
	                    String checkboxValue = checkbox.getAttribute("value");

	                    if ((checkboxText.equalsIgnoreCase(value) || checkboxValue.equalsIgnoreCase(value))
	                            && !checkbox.isSelected()) {
	                        checkbox.click();
	                        break; // move to next value after selecting
	                    }
	                }
	            }
	        }

	        /**
	         * Sets a toggle switch to the desired state (ON/OFF)
	         *
	         * @param driver The WebDriver instance
	         * @param toggleLocator Locator for the toggle element
	         * @param turnOn true to turn ON, false to turn OFF
	         */
	        public static void setToggle(WebDriver driver, By toggleLocator, boolean turnOn) {
	            WebElement toggle = driver.findElement(toggleLocator);

	            boolean isOn;

	            // Check if it's an input checkbox type toggle
	            if (toggle.getTagName().equalsIgnoreCase("input") &&
	                "checkbox".equalsIgnoreCase(toggle.getAttribute("type"))) {
	                isOn = toggle.isSelected();
	            }
	            else {
	                // Fallback: determine ON/OFF by class or aria attributes
	                String classAttr = toggle.getAttribute("class");
	                String ariaChecked = toggle.getAttribute("aria-checked");

	                isOn = (ariaChecked != null && ariaChecked.equalsIgnoreCase("true")) ||
	                       (classAttr != null && classAttr.toLowerCase().contains("on"));
	            }

	            // Click only if state needs to be changed
	            if (turnOn && !isOn) {
	                toggle.click();
	            } else if (!turnOn && isOn) {
	                toggle.click();
	            }
	        }

	        /**
	         * Moves a slider to a specific value and prints the slider value after moving.
	         *
	         * @param driver        The WebDriver instance
	         * @param sliderLocator Locator for the slider element
	         * @param targetValue   Desired slider value (as int)
	         */
	        public static void setSliderValue(WebDriver driver, By sliderLocator, int targetValue) {
	            WebElement slider = driver.findElement(sliderLocator);
	            Actions actions = new Actions(driver);

	            // If slider has value attribute (like <input type='range'>)
	            String minAttr = slider.getAttribute("min");
	            String maxAttr = slider.getAttribute("max");

	            if (minAttr != null && maxAttr != null) {
	                int min = Integer.parseInt(minAttr);
	                int max = Integer.parseInt(maxAttr);
	                int currentValue = Integer.parseInt(slider.getAttribute("value"));

	                int sliderWidth = slider.getSize().width;
	                double valuePerPixel = (double) (max - min) / sliderWidth;

	                int moveOffset = (int) ((targetValue - currentValue) / valuePerPixel);

	                actions.clickAndHold(slider).moveByOffset(moveOffset, 0).release().perform();
	            } else {
	                // Fallback for custom sliders (no min/max/value)
	                actions.dragAndDropBy(slider, targetValue, 0).perform();
	            }

	            // Show slider value after move
	            String newValue = slider.getAttribute("value");
	            if (newValue != null) {
	                System.out.println("Slider value after sliding: " + newValue);
	            } else {
	                System.out.println("Slider moved, but value not available in attribute.");
	            }
	        }
	        /**
	         * Drags a source element and drops it onto a target element.
	         *
	         * @param driver       The WebDriver instance
	         * @param sourceLocator Locator for the element to drag
	         * @param targetLocator Locator for the element to drop onto
	         */
	        public static void dragAndDrop(WebDriver driver, By sourceLocator, By targetLocator) {
	            WebElement source = driver.findElement(sourceLocator);
	            WebElement target = driver.findElement(targetLocator);

	            Actions actions = new Actions(driver);
	            actions.dragAndDrop(source, target).perform();

	            System.out.println("Dragged element from " + sourceLocator + " to " + targetLocator);
	        }
	        /**
	         * Uploads a file by sending the file path to an input[type='file'] element.
	         *
	         * @param driver     The WebDriver instance
	         * @param fileInputLocator Locator for the file input element
	         * @param filePath   Absolute path of the file to upload
	         */
	        public static void uploadFile(WebDriver driver, By fileInputLocator, String filePath) {
	            WebElement fileInput = driver.findElement(fileInputLocator);
	            fileInput.sendKeys(filePath);

	            System.out.println("File uploaded: " + filePath);
	        }
	        /**
	         * Uploads a folder using an <input type="file" webkitdirectory> element.
	         *
	         * @param driver          The WebDriver instance
	         * @param folderInputLocator Locator for the folder input element
	         * @param folderPath      Absolute path of the folder to upload
	         */
	        public static void uploadFolder(WebDriver driver, By folderInputLocator, String folderPath) {
	            WebElement folderInput = driver.findElement(folderInputLocator);
	            folderInput.sendKeys(folderPath);

	            System.out.println("Folder uploaded: " + folderPath);
	        }

	        /**
	         * Clicks a download link or button to download a file.
	         *
	         * @param driver The WebDriver instance
	         * @param downloadLocator Locator for the download link/button
	         */
	        public static void downloadFile(WebDriver driver, By downloadLocator) {
	            WebElement downloadButton = driver.findElement(downloadLocator);
	            downloadButton.click();
	            System.out.println("Download initiated for: " + downloadLocator);
	        }
	        /**
	         * Switches to an iframe by index, name/id, or locator.
	         *
	         * @param driver        The WebDriver instance
	         * @param frameIdentifier Can be Integer (index), String (name/id), or By (locator)
	         */
	        public static void switchToFrame(WebDriver driver, Object frameIdentifier) {
	            try {
	                if (frameIdentifier instanceof Integer) {
	                    driver.switchTo().frame((Integer) frameIdentifier);
	                    System.out.println("Switched to iframe by index: " + frameIdentifier);
	                }
	                else if (frameIdentifier instanceof String) {
	                    driver.switchTo().frame((String) frameIdentifier);
	                    System.out.println("Switched to iframe by name/id: " + frameIdentifier);
	                }
	                else if (frameIdentifier instanceof By) {
	                    WebElement frameElement = driver.findElement((By) frameIdentifier);
	                    driver.switchTo().frame(frameElement);
	                    System.out.println("Switched to iframe by locator: " + frameIdentifier);
	                }
	                else {
	                    throw new IllegalArgumentException("Invalid frameIdentifier type. Use index (int), name/id (String), or locator (By).");
	                }
	            } catch (Exception e) {
	                System.err.println("Error switching to iframe: " + e.getMessage());
	            }
	        }

	        /**
	         * Switches back to the main page content from any iframe.
	         */
	        public static void switchToDefault(WebDriver driver) {
	            driver.switchTo().defaultContent();
	            System.out.println("Switched back to default content.");
	        }
	        /**
	         * Searches a dynamic table for a given text and clicks it if found.
	         *
	         * @param driver The WebDriver instance
	         * @param tableLocator Locator for the table element
	         * @param searchText The text to search for in the table
	         * @return true if found and clicked, false otherwise
	         */
	        public static boolean searchAndClickInTable(WebDriver driver, By tableLocator, String searchText) {
	            WebElement table = driver.findElement(tableLocator);

	            // Get all rows
	            List<WebElement> rows = table.findElements(By.tagName("tr"));

	            for (int i = 0; i < rows.size(); i++) {
	                List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));

	                for (WebElement cell : cells) {
	                    if (cell.getText().trim().equalsIgnoreCase(searchText)) {
	                        cell.click();
	                        System.out.println("Found and clicked: " + searchText + " in row " + (i + 1));
	                        return true;
	                    }
	                }
	            }
	            System.out.println("Text not found in table: " + searchText);
	            return false;
	        }

	        /**
	         * Expands an accordion section by matching header text.
	         *
	         * @param driver          The WebDriver instance
	         * @param accordionHeadersLocator Locator for all accordion headers
	         * @param headerText      The visible text of the accordion section to expand
	         * @return true if found and clicked, false otherwise
	         */
	        public static boolean expandAccordion(WebDriver driver, By accordionHeadersLocator, String headerText) {
	            List<WebElement> headers = driver.findElements(accordionHeadersLocator);

	            for (WebElement header : headers) {
	                if (header.getText().trim().equalsIgnoreCase(headerText)) {
	                    header.click();
	                    System.out.println("Expanded accordion: " + headerText);
	                    return true;
	                }
	            }
	            System.out.println("Accordion header not found: " + headerText);
	            return false;
	        }

	        /**
	         * Collapses an accordion section by clicking the same header again.
	         *
	         * @param driver          The WebDriver instance
	         * @param accordionHeadersLocator Locator for all accordion headers
	         * @param headerText      The visible text of the accordion section to collapse
	         * @return true if found and clicked, false otherwise
	         */
	        public static boolean collapseAccordion(WebDriver driver, By accordionHeadersLocator, String headerText) {
	            return expandAccordion(driver, accordionHeadersLocator, headerText);
	        }

	        /**
	         * Waits for a toast to appear, captures its text, and waits until it disappears.
	         *
	         * @param driver        The WebDriver instance
	         * @param toastLocator  Locator for the toast element
	         * @param timeoutSec    Max wait time for toast appearance and disappearance
	         * @return The toast text
	         */
	        public static String captureToast(WebDriver driver, By toastLocator, int timeoutSec) {
	            WebDriverWait wait = new WebDriverWait(driver, timeoutSec);

	            // Wait until toast appears
	            WebElement toastElement = wait.until(
	                    ExpectedConditions.visibilityOfElementLocated(toastLocator)
	            );

	            String toastText = toastElement.getText();
	            System.out.println("Toast appeared: " + toastText);

	            // Wait until toast disappears
	            wait.until(ExpectedConditions.invisibilityOfElementLocated(toastLocator));
	            System.out.println("Toast disappeared.");

	            return toastText;
	        }

	        /**
	         * Waits for an alert, handles it (accept or dismiss), and returns its text.
	         *
	         * @param driver     The WebDriver instance
	         * @param timeoutSec Max wait time for alert (seconds)
	         * @param accept     true to accept alert, false to dismiss
	         * @return The alert text message
	         */
	        public static String handleAlert(WebDriver driver, int timeoutSec, boolean accept) {
	            WebDriverWait wait = new WebDriverWait(driver, timeoutSec);

	            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
	            String alertText = alert.getText();

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
	         * Waits for a confirm alert, accepts or dismisses it, and returns the alert text.
	         *
	         * @param driver     The WebDriver instance
	         * @param timeoutSec Max wait time for alert (seconds)
	         * @param accept     true to click OK (accept), false to click Cancel (dismiss)
	         * @return The alert text
	         */
	        public static String handleConfirmAlert(WebDriver driver, int timeoutSec, boolean accept) {
	            WebDriverWait wait = new WebDriverWait(driver, timeoutSec);

	            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
	            String alertText = alert.getText();

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
	         * Handles a prompt alert by sending input text, accepting or dismissing it, and returning the alert text.
	         *
	         * @param driver      The WebDriver instance
	         * @param timeoutSec  Max wait time for the alert (seconds)
	         * @param inputText   The text to send to the prompt input box
	         * @param accept      true to accept the prompt, false to dismiss
	         * @return The alert text before input was sent
	         */
	        public static String handlePromptAlert(WebDriver driver, int timeoutSec, String inputText, boolean accept) {
	            WebDriverWait wait = new WebDriverWait(driver, timeoutSec);

	            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
	            String alertText = alert.getText();

	            alert.sendKeys(inputText);

	            if (accept) {
	                alert.accept();
	                System.out.println("Prompt alert accepted with input: " + inputText);
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
			 * @param driver      WebDriver instance
			 * @param locator     Locator of the download element
			 * @param downloadDir Path of the download directory
			 * @param fileName    Expected file name
			 * @param timeoutSec  Max wait time in seconds
			 * @return true if file downloaded, false otherwise
			 */
			public static boolean downloadFile(WebDriver driver, By locator, String downloadDir, String fileName, int timeoutSec) {
				try {
					// Click on download element
					WebElement downloadBtn = driver.findElement(locator);
					downloadBtn.click();

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
			 * @param driver   WebDriver instance
			 * @param fileName Desired file name (without extension)
			 * @return The absolute path of the saved screenshot
			 */
			public static String captureScreenshot(WebDriver driver, String fileName) {
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


		}
