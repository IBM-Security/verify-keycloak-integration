package com.ibm.security.bdd.steps;

import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.ibm.security.bdd.util.TestSetup;
import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.ibm.security.bdd.containers.*;

public class HomepageSteps {
	
	private WebDriver driver = WebDriverFactory.getDriver();
	private HomepageContainer HomepageContainer = new HomepageContainer();

	@Given("^Customer navigate to the Keycloak Homepage$")
	public void customer_navigate_to_the_Keycloak_Homepage() throws Throwable {
	    driver.get(TestSetup.getServerBaseURL());
	}
	
	@Then("^Customer verifies the homepage displays Keycloak icon$")
	public void customer_verifies_the_homepage_displays_Keycloak_icon() throws Throwable {
	    TestUtils.assertElementAppears(HomepageContainer.KeycloakIcon);
	} 
	
	@Then("^Customer verifies the homepage displays Welcome to Keycloak message$")
	public void customer_verifies_the_homepage_displays_Welcome_to_Keycloak_message() throws Throwable {
		TestUtils.assertTextAppears(HomepageContainer.WelcomeToKCMessage, "Welcome to Keycloak");
	}

	@Then("^Customer verifies the homepage displays Administration Console hyperlink$")
	public void customer_verifies_the_homepage_displays_Administration_Console_hyperlink() throws Throwable {
		TestUtils.assertElementAppears(HomepageContainer.AdminConsoleLink);
		TestUtils.assertTextAppears(HomepageContainer.AdminConsoleLink, "Administration Console");
	}

	@Then("^Customer verifies the homepage displays Administration Console Description$")
	public void customer_verifies_the_homepage_displays_Administration_Console_Description() throws Throwable {
		TestUtils.assertTextAppears(HomepageContainer.AdminConsoleDescription, "Centrally manage all aspects of the Keycloak server");
	}

	@Then("^Customer verifies the homepage displays Documentation hyperlink$")
	public void customer_verifies_the_homepage_displays_Documentation_hyperlink() throws Throwable {
		TestUtils.assertElementAppears(HomepageContainer.DocumentationLink);
		TestUtils.assertTextAppears(HomepageContainer.DocumentationLink, "Documentation");
	}

	@Then("^Customer verifies the homepage displays Documentation Description$")
	public void customer_verifies_the_homepage_displays_Documentation_Description() throws Throwable {
		TestUtils.assertTextAppears(HomepageContainer.DocumentationDescription, "User Guide, Admin REST API and Javadocs");
	}

	@Then("^Customer verifies the homepage displays Keycloak Project hyperlink$")
	public void customer_verifies_the_homepage_displays_Keycloak_Project_hyperlink() throws Throwable{
		TestUtils.assertElementAppears(HomepageContainer.KeycloakProjectLink);
		TestUtils.assertTextAppears(HomepageContainer.KeycloakProjectLink, "Keycloak Project");
	}

	@Then("^Customer verifies the homepage displays Mailing List hyperlink$")
	public void customer_verifies_the_homepage_displays_Mailing_List_hyperlink() throws Throwable {
		TestUtils.assertElementAppears(HomepageContainer.MailingListLink);
		TestUtils.assertTextAppears(HomepageContainer.MailingListLink, "Mailing List");
	}

	@Then("^Customer verifies the homepage displays Report an issue hyperlink$")
	public void customer_verifies_the_homepage_displays_Report_an_issue_hyperlink() throws Throwable {
		TestUtils.assertElementAppears(HomepageContainer.ReportAnIssueLink);
		TestUtils.assertTextAppears(HomepageContainer.ReportAnIssueLink, "Report an issue");
	}
	
}
