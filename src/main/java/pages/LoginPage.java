package pages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.ConfigReader;
import utilities.ElementUtil;
import utilities.PasswordEncryption;

public class LoginPage {

    private WebDriver driver;
    private ElementUtil elementUtil;
    private PasswordEncryption pwencrypt;
    private Properties prop;

    // Constants
    private static final String JOBID_PREFIX = "JOBID_";
    private static final String RESUME_EXTENSION = ".pdf";
    private static final By SNACKBAR_LOCATOR = By.cssSelector("simple-snack-bar, mat-snack-bar-container");
    private static final int DEFAULT_WAIT_SECONDS = 20;
    private static final int UPLOAD_RETRIES = 2;

    public boolean isUploaded;

    // Locators
    @FindBy(xpath = "//span[normalize-space(text())='Login to app']")
    private WebElement loginToTalentHub;

    @FindBy(xpath = "//input[@type='email']")
    private WebElement textInput;

    @FindBy(id = "idSIButton9")
    private WebElement signInOrNextButton; // reused for Next / Yes / SignIn

    @FindBy(xpath = "//*[@name='passwd']")
    private WebElement passwordInput;

    @FindBy(xpath = "//span[contains(text(), 'Welcome Deepak Mahapatra')]")
    private WebElement welcomeText;

    @FindBy(xpath = "//span[contains(text(), 'Candidates')]")
    private WebElement candidateMenuBtn;

    @FindBy(xpath = "//span[contains(text(), 'Create Candidate')]")
    private WebElement createCandidateBtn;

    @FindBy(xpath = "//button[@role='switch' and @type='button']")
    private WebElement manualUploadButton;

    @FindBy(xpath = "//input[@type='file']")
    private WebElement fileInput;

    @FindBy(xpath = "//mat-select[@formcontrolname='location']")
    private WebElement locationDropdown;

    @FindBy(xpath = "//mat-select[@formcontrolname='profileSource']")
    private WebElement profileSourceDropdown;

    @FindBy(xpath = "//mat-select[@formcontrolname='agency']")
    private WebElement vendorDropdown;

    @FindBy(xpath = "//input[@role='combobox']")
    private WebElement jobIdInput;

    @FindBy(xpath = "//button[.//span[normalize-space(text())='Create Candidate']]")
    private WebElement createCandidateBtnInside;

    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.elementUtil = new ElementUtil(driver);
        this.pwencrypt = new PasswordEncryption();
        this.prop = new ConfigReader().init_prop();
        elementUtil.implicitWait(60);
        PageFactory.initElements(driver, this);
    }

    // ---------- Basic Actions ----------

    public void userEntersUrl() {
        driver.get(prop.getProperty("url"));
    }

    public void clickPlayGroundMenuBtn() {
        elementUtil.safeClick(loginToTalentHub);
    }

    public void switchToNewTab() {
        elementUtil.switchToNewTab();
    }

    public void switchToParentTab() {
        elementUtil.switchToParentTab();
    }

    public void enterUserID(String userID) throws Exception {
        elementUtil.input(textInput, userID);
    }

    public void clickSignInOrNextButton() {
        elementUtil.safeClick(signInOrNextButton);
    }

    public void enterPassword(String password) throws Exception {
        elementUtil.explicitWait(passwordInput,30);
        elementUtil.input(passwordInput, password);
    }

    /**
     * Robust MFA handling.
     * Handles popups, alerts, and window closures safely.
     */

   /* public void handleMfaAlert() {
        String parentWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();

        // Check for MFA window
        for (String handle : allWindows) {
            if (!handle.equals(parentWindow)) {
                // MFA opens in a new window
                driver.switchTo().window(handle);
                System.out.println("Switched to MFA popup: " + handle);

                // Click Yes
                elementUtil.safeClick(signInOrNextButton);

                // Close MFA popup
                driver.close();
                System.out.println("Closed MFA popup: " + handle);

                // Switch back to parent login window
                driver.switchTo().window(parentWindow);
                System.out.println("Switched back to main window: " + parentWindow);
                return;
            }
        }

        // If MFA is in same window
        System.out.println("No separate MFA popup found, clicking Yes in main window if present.");
        elementUtil.safeClick(signInOrNextButton);
    }*/


    public void safeClickYesButton() {
        try {
            String parentWindow = driver.getWindowHandles().iterator().next();

            // Identify login window
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(parentWindow)) {
                    driver.switchTo().window(handle);

                    // Wait until Yes button is clickable
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.elementToBeClickable(signInOrNextButton));

                    // Click Yes
                    signInOrNextButton.click();
                    System.out.println("Clicked Yes button successfully");

                    // Wait until popup closes automatically
                    new WebDriverWait(driver, Duration.ofSeconds(15))
                            .until(d -> d.getWindowHandles().size() == 1);

                    // Switch back to parent window
                    driver.switchTo().window(parentWindow);
                    System.out.println("Switched back to main application window.");
                    return;
                }
            }

            // Case: No popup, Yes is in the same window
            elementUtil.safeClick(signInOrNextButton);
            System.out.println("Clicked Yes button successfully (same window)");

        } catch (Exception e) {
            throw new RuntimeException("Failed to click Yes button: " + e.getMessage(), e);
        }
    }


    public String getWelcomeText() {
        return welcomeText.getText();
    }

    // ---------- Candidate Actions ----------

    public void clickCandidateMenuBtn() {
        elementUtil.safeClick(candidateMenuBtn);
    }

    public void clickCreateCandidateBtn() {
        elementUtil.safeClick(createCandidateBtn);
        elementUtil.clickSwitch(manualUploadButton);
    }

    public void uploadResumeOfCandidate(String filePath) {
        this.isUploaded = false;
        for (int attempt = 1; attempt <= UPLOAD_RETRIES; attempt++) {
            try {
                boolean uploaded = elementUtil.uploadFileGeneric(fileInput, filePath);
                if (uploaded) {
                    this.isUploaded = true;
                    System.out.println("Resume uploaded successfully: " + filePath);
                    break;
                } else {
                    System.err.println("Upload attempt " + attempt + " failed for: " + filePath);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("Exception during upload attempt " + attempt + ": " + e.getMessage());
            }
        }
    }

    public void selectLocation(String locationName) {
        elementUtil.selectMatOption(locationDropdown, locationName);
        elementUtil.goToBottom();
    }

    public void selectProfileSource(String profileSource) {
        elementUtil.selectMatOption(profileSourceDropdown, profileSource);
    }

    public void selectVendor(String vendor) {
        elementUtil.selectMatOptionByTyping(vendorDropdown, vendor);
    }

    public void enterJobId(String jobId) {
        elementUtil.waitForDomLoad(40);
        elementUtil.explicitWait(jobIdInput,30);
        elementUtil.input(jobIdInput, jobId);
    }

    public void clickCreateCandidateBtnInside() {
        elementUtil.safeClick(createCandidateBtnInside);
    }

    // ---------- Candidate Creation From Folder ----------

    public boolean createCandidatesFromFolder(String path) {
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory())
            throw new RuntimeException("Invalid folder: " + path);

        File[] jobFolders = folder.getName().startsWith(JOBID_PREFIX)
                ? new File[]{folder}
                : folder.listFiles(f -> f.isDirectory() && f.getName().startsWith(JOBID_PREFIX));

        if (jobFolders == null || jobFolders.length == 0)
            throw new RuntimeException("No jobId folders found under: " + path);

        boolean anyUploaded = false;
        int totalExpected = 0, totalAttempted = 0, totalSuccess = 0, totalErrors = 0;

        for (File jobFolder : jobFolders) {
            String jobId = jobFolder.getName().split("_", 2)[1];
            File[] locationFolders = jobFolder.listFiles(File::isDirectory);
            if (locationFolders == null || locationFolders.length == 0) {
                System.err.println("⚠️ No location folders under job folder: " + jobId);
                continue;
            }

            List<String> errorDetails = new ArrayList<>();
            int jobExpected = 0, jobAttempted = 0, jobSuccess = 0, jobErrors = 0;

            // Count expected resumes
            for (File locFolder : locationFolders)
                for (File psFolder : locFolder.listFiles(File::isDirectory))
                    for (File vendorFolder : psFolder.listFiles(File::isDirectory)) {
                        File[] resumes = vendorFolder.listFiles(f -> f.isFile() && f.getName().endsWith(RESUME_EXTENSION));
                        if (resumes != null) jobExpected += resumes.length;
                    }

            // Process resumes
            for (File locFolder : locationFolders) {
                String location = locFolder.getName();
                for (File psFolder : locFolder.listFiles(File::isDirectory)) {
                    String profileSource = psFolder.getName();
                    for (File vendorFolder : psFolder.listFiles(File::isDirectory)) {
                        String vendor = vendorFolder.getName();
                        File[] resumes = vendorFolder.listFiles(f -> f.isFile() && f.getName().endsWith(RESUME_EXTENSION));
                        if (resumes == null) continue;

                        for (File resume : resumes) {
                            try {
                                uploadResumeOfCandidate(resume.getAbsolutePath());
                                if (!isUploaded) {
                                    System.err.println("❌ Skipping resume (upload failed twice): " + resume.getName());
                                    continue;
                                }

                                jobAttempted++;

                                // Fill candidate details
                                selectLocation(location);
                                selectProfileSource(profileSource);
                                selectVendor(vendor);
                                enterJobId(jobId);
                                clickCreateCandidateBtnInside();

                                // Wait for snackbar
                                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                                try {
                                    WebElement snackbar = wait.until(ExpectedConditions.visibilityOfElementLocated(SNACKBAR_LOCATOR));
                                    String text = snackbar.getText().trim();
                                    if (text.contains("Error creating candidate")) {
                                        jobErrors++;
                                        errorDetails.add(resume.getName() + " → " + text);
                                    } else {
                                        jobSuccess++;
                                        anyUploaded = true;
                                        System.out.printf("✅ Created resume: %s | JobId=%s, Location=%s, ProfileSource=%s, Vendor=%s%n",
                                                resume.getName(), jobId, location, profileSource, vendor);
                                    }
                                } catch (Exception e) {
                                    System.err.println("⚠️ Snackbar not found for: " + resume.getName());
                                }

                                // Refresh candidate creation page
                                clickCandidateMenuBtn();
                                wait.until(ExpectedConditions.elementToBeClickable(createCandidateBtn));
                                clickCreateCandidateBtn();

                            } catch (Exception e) {
                                System.err.println("❌ Failed to create candidate for resume: " + resume.getName());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            // Job summary
            System.out.printf("JobId %s → Expected=%d, Attempted=%d, Success=%d, Errors=%d%n",
                    jobId, jobExpected, jobAttempted, jobSuccess, jobErrors);
            if (!errorDetails.isEmpty()) {
                System.out.println("Failed resumes:");
                errorDetails.forEach(System.out::println);
            }

            totalExpected += jobExpected;
            totalAttempted += jobAttempted;
            totalSuccess += jobSuccess;
            totalErrors += jobErrors;
        }

        // Delete folder after processing
       /* try { deleteDirectoryRecursively(folder); System.out.println("Folder deleted: " + folder.getAbsolutePath()); }
        catch (IOException e) { System.err.println("Failed to delete folder: " + folder.getAbsolutePath()); }*/

        // Grand summary
        System.out.printf("GRAND TOTAL → Expected=%d, Attempted=%d, Success=%d, Errors=%d%n",
                totalExpected, totalAttempted, totalSuccess, totalErrors);

        return anyUploaded;
    }

    // ---------- Helpers ----------

    private void deleteDirectoryRecursively(File folder) throws IOException {
        if (folder.isDirectory())
            for (File child : folder.listFiles())
                deleteDirectoryRecursively(child);
        Files.delete(folder.toPath());
    }
}
