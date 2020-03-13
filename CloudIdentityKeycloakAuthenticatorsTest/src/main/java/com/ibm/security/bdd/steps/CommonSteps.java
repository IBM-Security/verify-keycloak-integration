package com.ibm.security.bdd.steps;

import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import com.ibm.security.bdd.containers.CommonContainer;
import com.ibm.security.bdd.containers.LoginContainer;
import com.ibm.security.bdd.containers.DashboardContainer;
import com.ibm.security.bdd.util.TestSetup;
import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.java.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class CommonSteps {

	public static final String UI_CONTEXT_ROOT = "ui";

	private WebDriver driver = WebDriverFactory.getDriver();
	private CommonContainer commonContainer = new CommonContainer();
	private LoginContainer loginContainer = new LoginContainer();
	private DashboardContainer dashboardContainer = new DashboardContainer();

	@Given("^\"(.*?)\" logs into the \"(.*?)\" and navigate to \"(.*?)\"$")
	public void logs_into_the_and_navigate_to(String username, String destination, String navigationLink) throws Throwable {
		customer_logs_in_with_username(destination, username);
		Admin_Navigates_To_On_The_Admin_Console(navigationLink);
	}
	
	//destination can be Admmin Console or User Console
	@Given("^Customer logs into the \"(.*?)\" with username \"(.*?)\"$")
	public void customer_logs_in_with_username(String destination, String username) throws Throwable {
		String baseUrl = TestSetup.getServerBaseURL();
		String loadUrl;
		String password = null;

		if (destination.equals("Admin Console")) {
			loadUrl = baseUrl + "admin";
			password = TestSetup.getPasswordForPerson(username);
		} else {
			loadUrl = baseUrl + "realms/test-realm/account";
			password = TestSetup.getPasswordForUser(username);
		}
		
		String userName = TestSetup.getUserNameForPerson(username);

		driver.get(loadUrl);
		driver.manage().window().maximize();

		TestUtils.assertElementAppears(loginContainer.UsernameText);
		TestUtils.verifiedSendKeys(loginContainer.UsernameText, userName);
		TestUtils.assertElementAppears(loginContainer.PasswordText);
		TestUtils.verifiedSendKeys(loginContainer.PasswordText, password);
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		loginContainer.LoginButton.click();
		Thread.sleep(3000);
	}
	
	@Given("^\"(.*?)\" logs into the \"(.*?)\" using \"(.*?)\"$")
	public void commonLogin(String person, String destination, String logInType) throws Throwable {
		String baseUrl = TestSetup.getServerBaseURL();
		driver.get(baseUrl);

		String userName = TestSetup.getUserNameForPerson(person);
		String password = TestSetup.getPasswordForPerson(person);
		
		TestUtils.assertElementAppears(commonContainer.usernameInput);
		TestUtils.verifiedSendKeys(commonContainer.usernameInput, userName);
		TestUtils.assertElementAppears(commonContainer.passwordInput);
		TestUtils.verifiedSendKeys(commonContainer.passwordInput, password);
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		commonContainer.loginButton.click();
	}
	
	@Then("^Admin navigates to \"(.*?)\" on the Admin Console$")
	public void Admin_Navigates_To_On_The_Admin_Console(String navLink) throws Throwable {
		if (navLink.equals("Realm Settings")) {
			dashboardContainer.RealmSettings.click();;
		} else if (navLink.equals("Clients")) {
			dashboardContainer.Clients.click();
		} else if (navLink.equals("Client Scopes")) {
			dashboardContainer.ClientScopes.click();
		} else if (navLink.equals("Roles")) {
			dashboardContainer.Roles.click();
		} else if (navLink.equals("Identity Providers")) {
			dashboardContainer.IdentityProviders.click();
		} else if (navLink.equals("User Federation")) {
			dashboardContainer.UserFederation.click();
		} else if (navLink.equals("Authentication")) {
			dashboardContainer.Authentication.click();
		} else if (navLink.equals("Groups")) {
			dashboardContainer.Groups.click();
		} else if (navLink.equals("Users")) {
			dashboardContainer.Users.click();
		} else if (navLink.equals("Sessions")) {
			dashboardContainer.Sessions.click();
		} else if (navLink.equals("Events")) {
			dashboardContainer.Events.click();
		} else if (navLink.equals("Import")) {
			dashboardContainer.Import.click();
		} else if (navLink.equals("Export")) {
			dashboardContainer.Export.click();
		}
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	}
	
	@Then("^Customer navigates to path \"(.*?)\" on current tenant in browser session$")
	public void customerNavigatesToPath(String path) throws Throwable {
		String newUrl = "";
		String currentUrl = driver.getCurrentUrl();
		newUrl = currentUrl.replaceAll("(\\.[a-z]+)(\\/.*)$", "$1" + path);
		driver.get(newUrl);
	}
	
	@After
	public void afterScenario(Scenario scenario) {
		if (scenario.isFailed()) {
			String failureTimeStamp = TestUtils.getEasternTime("yyyy/MM/dd HH:mm:ss") + " EASTERN_TIMEZONE";
			DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			dateformat.setTimeZone(TimeZone.getTimeZone(TestUtils.EASTERN_TIMEZONE));
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, -120);
			String tenantUnderTest = TestSetup.getServerBaseURL();

			String browserUnderTest = TestSetup.getGridBrowserName();
			String currentUrl = driver.getCurrentUrl();
			String browserConsoleLogString = null;
			if (TestSetup.getGridBrowserName().equals("chrome")) {
				LogEntries browserConsolelogEntries = driver.manage().logs().get(LogType.BROWSER);
				browserConsoleLogString = "";
				int counterLoop = 0;
				for (LogEntry entry : browserConsolelogEntries) {
					browserConsoleLogString += entry.toString() + "<br>";
					counterLoop++;
				}
				if (counterLoop == 0) {
					browserConsoleLogString += "There was no console logging during this test session";
				}
			}

			String base64screenshot = "";
			if (driver instanceof TakesScreenshot) {
				base64screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
			}

			String newcapturedDOM = driver.getPageSource();

			String dataCollectionHTML = "<h2 style=\"text-align:center;\">Test Automation - Runtime Failure Data Collection</h2>"
					+ "<h3>The failure occurred at: " + failureTimeStamp + " </h3>"
					+ "<h3>The failure occurred on tenant: " + tenantUnderTest + "</h3>"
					+ "<h3>The failure occurred on browser: " + browserUnderTest + "</h3>" + "<h3>The current url is: "
					+ currentUrl + "</h3>" + " <br> ";

			/*
			 * Add console log to output if the browser supports it.
			 */
			if (browserConsoleLogString == null) {
				dataCollectionHTML += "<h3 style=\"color:red\">The browser console log data is not available for this browser type ("
						+ TestSetup.getGridBrowserName() + ")" + "</h3> <br> ";
			} else {
				dataCollectionHTML += "<h3>The browser console log data is:" + "</h3> <br> "
						+ browserConsoleLogString;
			}

			dataCollectionHTML += "<h3>Screenshot:</h3> <br> " + "<img src='data:image/jpeg;base64,"
					+ base64screenshot + "' />" + "<h3> Page Source: </h3> <br> <details> <summary>  </summary>"
					+ "<textarea style=\"width:100%; height:500px\">" + newcapturedDOM + "</textarea>" + "</details>";

			try {
				scenario.embed(dataCollectionHTML.getBytes(), "text/html");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		WebDriverFactory.clearDriver();
	}
}