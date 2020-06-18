package com.ibm.security.bdd.steps;

import org.openqa.selenium.WebDriver;

import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.java.en.Then;

public class DemoSteps {
	private WebDriver driver = WebDriverFactory.getDriver();

	@Then("Test step")
	public void testStep() throws Throwable {
		// driver.get(TestSetup.getServerBaseURL());
		driver.get("https://www.google.com");
	}
}