package com.ibm.security.bdd.steps;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ibm.security.bdd.containers.UsersContainer;
import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.java.en.Then;

public class UsersSteps {
	private WebDriver driver = WebDriverFactory.getDriver();
	private UsersContainer UsersContainer = new UsersContainer();
	@Then("^Customer clicks on Users Link$")
	public void customer_clicks_on_Users_Link() throws Throwable{
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.findElement(By.linkText("Users")).click();
	}

	
	@Then("^Customer verifies Users page elements$")
	public void customer_verifies_users_page_elements() throws Throwable {
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		TestUtils.assertElementAppears(UsersContainer.UsersTitle);
		TestUtils.assertElementAppears(UsersContainer.LookupTab);
		TestUtils.assertElementAppears(UsersContainer.UserSearchText);
		TestUtils.assertElementAppears(UsersContainer.UserSearchButton);
		TestUtils.assertElementAppears(UsersContainer.viewAllUsersButton);
		TestUtils.assertElementAppears(UsersContainer.UnlockUsersButton);
		TestUtils.assertElementAppears(UsersContainer.AddUserButton);
		
	}
	
	@Then ("^Customer clicks Add new user button$")
	public void customer_clicks_Add_new_user_button() throws Throwable {
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		UsersContainer.viewAllUsersButton.click();
		TestUtils.assertElementAppears(UsersContainer.AddUserButton);
		UsersContainer.AddUserButton.click();
		//driver.findElement(By.cssSelector("#createUser")).click();
	}

	
	@Then("^Customer verifies elements on AddUser page$")
	public void customer_verifies_elements_on_AddUser_page() throws Throwable {
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		TestUtils.assertElementAppears(UsersContainer.AdduserTitle);
		TestUtils.assertElementAppears(UsersContainer.IDLabel);
		TestUtils.assertElementAppears(UsersContainer.ID);
		TestUtils.assertElementAppears(UsersContainer.UsernameLabel);
		TestUtils.assertElementAppears(UsersContainer.Username);
		TestUtils.assertElementAppears(UsersContainer.EmailLabel);
		TestUtils.assertElementAppears(UsersContainer.Email);
		TestUtils.assertElementAppears(UsersContainer.FirstNameLabel);
		TestUtils.assertElementAppears(UsersContainer.FirstName);
		TestUtils.assertElementAppears(UsersContainer.LastNameLabel);
		TestUtils.assertElementAppears(UsersContainer.LastName);
		TestUtils.assertElementAppears(UsersContainer.UserEnabledLabel);
		TestUtils.assertElementAppears(UsersContainer.EmailVerifiedLabel);
		TestUtils.assertElementAppears(UsersContainer.ReqUserActionLabel);
		TestUtils.assertElementAppears(UsersContainer.Save);
		TestUtils.assertElementAppears(UsersContainer.Cancel);
		Thread.sleep(1000);
		TestUtils.assertElementAppears(UsersContainer.UserEnabledON);
		TestUtils.assertElementAppears(UsersContainer.EmailVerifiedOFF);
		driver.findElement(By.cssSelector("#s2id_reqActions")).click();
		Thread.sleep(2000);
		TestUtils.assertElementAppears(UsersContainer.ConfigureOTP);
		TestUtils.assertElementAppears(UsersContainer.UpdatePassword);
		TestUtils.assertElementAppears(UsersContainer.UpdateProfile);
		TestUtils.assertElementAppears(UsersContainer.VerifyEmail);
		TestUtils.assertTextAppears(UsersContainer.UserEnabledON, "ON");
		System.out.println("User Enabled button is ON by default");
		TestUtils.assertTextAppears(UsersContainer.EmailVerifiedOFF, "OFF");
		System.out.println("Email Verified button is OFF by default");
		
	}
	

}
