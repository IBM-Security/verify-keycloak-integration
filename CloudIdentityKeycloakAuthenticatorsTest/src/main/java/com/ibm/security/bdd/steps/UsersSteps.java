package com.ibm.security.bdd.steps;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.ibm.security.bdd.containers.UsersContainer;
import com.ibm.security.bdd.containers.CommonContainer;
import com.ibm.security.bdd.util.TestSetup;
import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class UsersSteps {
	private WebDriver driver = WebDriverFactory.getDriver();
	private UsersContainer UsersContainer = new UsersContainer();
	private CommonContainer CommonContainer = new CommonContainer();

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
		driver.findElement(By.cssSelector("#createUser")).click();
	}

	
	@Then("^Customer verifies elements on AddUser page$")
	public void customer_verifies_elements_on_AddUser_page() throws Throwable {
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		TestUtils.assertElementAppears(UsersContainer.AdduserTitle);
		System.out.println(UsersContainer.AdduserTitle);
		TestUtils.assertElementAppears(UsersContainer.IDLabel);
		System.out.println(UsersContainer.IDLabel);
		TestUtils.assertElementAppears(UsersContainer.ID);
		System.out.println(UsersContainer.ID);
		TestUtils.assertElementAppears(UsersContainer.UsernameLabel);
		System.out.println(UsersContainer.UsernameLabel);
		TestUtils.assertElementAppears(UsersContainer.Username);
		System.out.println(UsersContainer.Username);
		TestUtils.assertElementAppears(UsersContainer.EmailLabel);
		System.out.println(UsersContainer.EmailLabel);
		TestUtils.assertElementAppears(UsersContainer.Email);
		System.out.println(UsersContainer.Email);
		TestUtils.assertElementAppears(UsersContainer.FirstNameLabel);
		System.out.println(UsersContainer.FirstNameLabel);
		TestUtils.assertElementAppears(UsersContainer.FirstName);
		System.out.println(UsersContainer.FirstName);
		TestUtils.assertElementAppears(UsersContainer.LastNameLabel);
		System.out.println(UsersContainer.LastNameLabel);
		TestUtils.assertElementAppears(UsersContainer.LastName);
		System.out.println(UsersContainer.LastName);
		TestUtils.assertElementAppears(UsersContainer.UserEnabledLabel);
		System.out.println(UsersContainer.UserEnabledLabel);
		TestUtils.assertElementAppears(UsersContainer.EmailVerifiedLabel);
		System.out.println(UsersContainer.EmailVerifiedLabel);
		TestUtils.assertElementAppears(UsersContainer.ReqUserActionLabel);
		System.out.println(UsersContainer.ReqUserActionLabel);
		TestUtils.assertElementAppears(UsersContainer.Save);
		System.out.println(UsersContainer.Save);
		TestUtils.assertElementAppears(UsersContainer.Cancel);
		System.out.println(UsersContainer.Cancel);
		//Thread.sleep(1000);
		TestUtils.assertElementAppears(UsersContainer.UserEnabledON);
		System.out.println(UsersContainer.UserEnabledON);
		//Thread.sleep(1000);
		//TestUtils.assertElementAppears(UsersContainer.EmailVerifiedOFF);
		//System.out.println(UsersContainer.EmailVerifiedOFF);
		
		driver.findElement(By.cssSelector("#s2id_reqActions")).click();
		Thread.sleep(2000);
		TestUtils.assertElementAppears(UsersContainer.ConfigureOTP);
		System.out.println(UsersContainer.ConfigureOTP);
		TestUtils.assertElementAppears(UsersContainer.UpdatePassword);
		System.out.println(UsersContainer.UpdatePassword);
		TestUtils.assertElementAppears(UsersContainer.UpdateProfile);
		System.out.println(UsersContainer.UpdateProfile);
		TestUtils.assertElementAppears(UsersContainer.VerifyEmail);
		System.out.println(UsersContainer.VerifyEmail);
	}
	

}
