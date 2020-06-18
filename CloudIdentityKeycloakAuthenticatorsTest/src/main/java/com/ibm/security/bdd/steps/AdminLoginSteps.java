package com.ibm.security.bdd.steps;

import com.ibm.security.bdd.containers.DashboardContainer;
import com.ibm.security.bdd.containers.HomepageContainer;
import com.ibm.security.bdd.containers.LoginContainer;
import com.ibm.security.bdd.util.TestUtils;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class AdminLoginSteps {

	private HomepageSteps HomepageSteps = new HomepageSteps();
	private HomepageContainer HomepageContainer = new HomepageContainer();
	private LoginContainer LoginContainer = new LoginContainer();
	private DashboardContainer DashboardContainer = new DashboardContainer();
	
	@Given("^Customer navigates to the Admin Console Login page$")
	public void customer_navigates_to_the_Admin_Console_Login_page() throws Throwable {
		HomepageSteps.customer_navigate_to_the_Keycloak_Homepage();
		HomepageContainer.AdminConsoleLink.click();
	}
	
	@Then("^Customer verifies the Keycloak icon is present$")
	public void customer_verifies_the_Keycloak_icon_is_present() throws Throwable {
		TestUtils.assertElementAppears(LoginContainer.KeycloakIcon);
	}

	@Then("^Customer verifies the Log In title is present$")
	public void customer_verifies_the_Log_In_title_is_present() throws Throwable {
		TestUtils.assertElementAppears(LoginContainer.LoginTitle);
	}

	@Then("^Customer verifies the Username field is present$")
	public void customer_verifies_the_Username_field_is_present() throws Throwable {
		TestUtils.assertElementAppears(LoginContainer.UsernameText);
	}

	@Then("^Customer verifies the Password field is present$")
	public void customer_verifies_the_Password_field_is_present() throws Throwable {
		TestUtils.assertElementAppears(LoginContainer.PasswordText);
	}

	@Then("^Customer verifies the Login button is present$")
	public void customer_verifies_the_Login_button_is_present() throws Throwable {
		TestUtils.assertElementAppears(LoginContainer.LoginButton);
	}

	@Then("^Customer verifies an error is displayed using an incorrect username$")
	public void customer_verifies_an_error_is_displayed_using_an_incorrect_username() throws Throwable {
		customer_logs_in_with_username_and_password("wrongusername", "admin");
		TestUtils.assertElementAppears(LoginContainer.ErrorIcon);
		TestUtils.assertTextAppears(LoginContainer.ErrorMessage, "Invalid username or password.");
	}

	@Then("^Customer verifies an error is displayed using an incorrect password$")
	public void customer_verifies_an_error_is_displayed_using_an_incorrect_password() throws Throwable {
		customer_logs_in_with_username_and_password("admin", "wrongpassword");
		TestUtils.assertElementAppears(LoginContainer.ErrorIcon);
		TestUtils.assertTextAppears(LoginContainer.ErrorMessage, "Invalid username or password.");
	}

	@Then("^Customer verifies an error is displayed using an empty username or password$")
	public void customer_verifies_an_error_is_displayed_using_an_empty_username_or_password() throws Throwable {
		customer_logs_in_with_username_and_password("", "");
		TestUtils.assertElementAppears(LoginContainer.ErrorIcon);
		TestUtils.assertTextAppears(LoginContainer.ErrorMessage, "Invalid username or password.");
	}

	@Then("^Customer logs in with username \"(.*?)\" and password \"(.*?)\"$")
	public void customer_logs_in_with_username_and_password(String username, String password) throws Throwable {
		TestUtils.assertElementAppears(LoginContainer.UsernameText);
		TestUtils.verifiedSendKeys(LoginContainer.UsernameText, username);
		TestUtils.assertElementAppears(LoginContainer.PasswordText);
		TestUtils.verifiedSendKeys(LoginContainer.PasswordText, password);
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		LoginContainer.LoginButton.click();
	}

	@Then("Customer verifies Admin Console login is successful")
	public void customer_verifies_Admin_Console_login_is_successful() throws Throwable {
		TestUtils.assertElementAppears(DashboardContainer.AdminDropDown);
		TestUtils.assertElementAppears(DashboardContainer.KeycloakIcon);
	}

}
