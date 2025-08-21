/*
 * 
 * 
 * @Author : Deepak Mahapatra
 * 
 */

package testrunners;

import org.testng.annotations.Test;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(tags = "@SmokeTest", features = { "src/test/resources/features/SmokeTest.feature" }, glue = { "stepdefinitions",
		"applicationhooks" }, plugin = { "pretty",
		"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:", "timeline:test-output-thread/" }, dryRun = false, monochrome = true, 
		publish = true)

/*@CucumberOptions(
features = { "src/test/resources/features" }, glue = { "stepdefinitions",
"applicationhooks" }, plugin = { "pretty",
		"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:", "timeline:test-output-thread/" }, dryRun = false, monochrome = true, 
		publish = true,
		tags= "@AffiliationPharmAdd or @AffiliationPharmTerm or @ENSContractManagementSmokeTest or @RateManagamentPharmacyListCreation or @BBCreation or @CNPCreation "
)*/

@Test
public class TestRunner extends AbstractTestNGCucumberTests {
	/*
	 * private TestNGCucumberRunner testNGCucumberRunner;
	 * 
	 * @BeforeClass(alwaysRun = true) public void setUpClass() {
	 * testNGCucumberRunner = new TestNGCucumberRunner(this.getClass()); }
	 *
	 * @Test(groups = "cucumber", description = "Runs Cucumber Scenarios",zr
	 * dataProvider = "scenarios") public void scenario(PickleWrapper pickle,
	 * FeatureWrapper cucumberFeature) {
	 * testNGCucumberRunner.runScenario(pickle.getPickle()); }
	 * 
	 * @DataProvider public Object[][] features() { return
	 * testNGCucumberRunner.provideScenarios(); }
	 * 
	 * @AfterClass(alwaysRun = true) public void tearDownClass() {
	 * testNGCucumberRunner.finish();
	 */
}
