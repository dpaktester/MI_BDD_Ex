package stepdefinitions;

import io.cucumber.java.en.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.LoginPage;
import factory.DriverFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

@Slf4j
public class SmokeTestSteps {

    private LoginPage loginPage;
    private WebDriver driver;

    @Given("I open the browser")
    public void i_open_the_browser() {
        driver = DriverFactory.getDriver();
        loginPage = new LoginPage(driver);
    }

    @Given("I navigate to url")
    public void i_navigate_to_url() {
        loginPage.userEntersUrl();
    }

    @When("User clicks on Login to App button")
    public void user_clicks_on_login_to_app_button() {
        loginPage.clickPlayGroundMenuBtn();
    }

    @And("User enters username {string}")
    public void user_enters_username(String username) throws Exception {
        loginPage.switchToNewTab();
        loginPage.enterUserID(username);
    }

    @And("User clicks on Next button")
    public void user_clicks_on_next_button() throws Exception {
        loginPage.clickSignInOrNextButton();
    }

    @And("User enters password {string}")
    public void user_enters_password(String password) throws Exception {
        loginPage.enterPassword(password);
    }

    @And("User clicks on Sign In button")
    public void user_clicks_on_sign_in_button() {

        loginPage.clickSignInOrNextButton();
    }

    @And("User clicks on Yes button")
    public void user_clicks_on_yes_button() throws Exception {
        //loginPage.handleMfaAlert();
        //loginPage.clickSignInOrNextButton();
        //loginPage.switchToParentTab();
        loginPage.safeClickYesButton();
        //loginPage.switchToParentTab();
    }

    @Then("User should be successfully logged in")
    public void user_should_be_successfully_logged_in() {
        String actualText = loginPage.getWelcomeText();
        String expectedText = "Welcome Deepak Mahapatra";
        Assert.assertEquals(actualText, expectedText, "The login success message does not match.");
    }

    @Then("User Clicks on Candidate Menu Button")
    public void user_Clicks_On_Candidate_Menu_Button() {
        loginPage.clickCandidateMenuBtn();
    }

    @And("User Clicks on Create Candidate Button")
    public void user_Clicks_On_Create_Candidate_Button() {
        loginPage.clickCreateCandidateBtn();
    }

    @And("User Clicks on Select File button and uploads the profile in pdf format")
    public void user_Clicks_On_Select_File_Button_And_Uploads_CV() {
        String filePath = System.getProperty("user.dir") + "\\ResumeOfCandidates\\Anurag Rajput.pdf";

        loginPage.uploadResumeOfCandidate(filePath);

        //Assert upload was successful (from generic uploader)
        Assert.assertTrue(loginPage.isUploaded, "Resume upload failed!");
    }

    @And("User Selects the Value from Location dropdown")
    public void user_Selects_the_Value_from_Location_Dropdown() {
        loginPage.selectLocation("Bengaluru");

    }

    @And("User Selects the Value from profile source dropdown")
    public void user_Selects_the_Value_from_profile_source_dropdown() {
        loginPage.selectProfileSource("Other"); // Uses generic selectMatOption
    }

    @And("User enters jobId")
    public void user_enters_jobId() throws Exception {
        loginPage.enterJobId("MEDQEM0015");
    }
    @And ("User Clicks on Create Candidate Button Inside")
    public void user_Clicks_on_Create_Candidate_Button_Inside()throws Exception{
        loginPage.clickCreateCandidateBtnInside();
        Thread.sleep(10000);
        System.out.println("Created Candidate Successfully");
    }
    @And("User uploads profiles with jobname")
    public void testBulkCandidateCreation() {
        LoginPage loginPage = new LoginPage(driver);

        // Root directory containing all job folders
        String rootPath = System.getProperty("user.dir");
        File rootDir = new File(rootPath);

        // Only include directories starting with JOBID_
        File[] jobFolders = rootDir.listFiles(file ->
                file.isDirectory() && file.getName().startsWith("JOBID_"));

        if (jobFolders == null || jobFolders.length == 0) {
            throw new RuntimeException("No JOBID_ folders found under: " + rootPath);
        }

        // Pick the latest JOBID_ folder
        File latestJobFolder = Arrays.stream(jobFolders)
                .max(Comparator.comparingLong(File::lastModified))
                .orElseThrow(() -> new RuntimeException("Unable to determine latest JOBID folder"));

        // Extract jobId (everything after underscore)
        String folderName = latestJobFolder.getName();  // e.g. JOBID_MEDOES0004
        String jobId = folderName.contains("_") ? folderName.split("_", 2)[1] : folderName;

        System.out.println("Using latest job folder: " + latestJobFolder.getAbsolutePath());
        System.out.println("Extracted JobId: " + jobId);

        boolean result = loginPage.createCandidatesFromFolder(latestJobFolder.getAbsolutePath());

        Assert.assertTrue(result,
                "Bulk candidate creation failed! No candidates uploaded from: " + latestJobFolder.getName());
    }



}
