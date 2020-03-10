package com.ibm.security.bdd.steps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import com.ibm.security.bdd.containers.AuthenticationContainer;
import com.ibm.security.bdd.containers.CommonContainer;
import com.ibm.security.bdd.containers.UsersContainer;
import com.ibm.security.bdd.util.TestUtils;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.security.bdd.containers.UsersContainer;
import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.java.en.Then;

public class AddDeleteUsersSteps {
	
	private WebDriver driver = WebDriverFactory.getDriver();
	private UsersContainer UsersContainer = new UsersContainer();
	private CommonContainer CommonContainer = new CommonContainer();

	@Then("^delete \"(.*?)\" if exists$")
	public void delete_if_exists(String username) throws Throwable {
		Thread.sleep(3000);
		TestUtils.verifiedSendKeys(UsersContainer.UserSearchText, username);
		Thread.sleep(3000);
		UsersContainer.UserSearchButton.click();
		Thread.sleep(3000);
		 List<WebElement> usernameExists = driver.findElements(By.xpath("//td[contains(text(),'"+username+"')]"));
	       if(usernameExists.size() == 0){
				System.out.println("User '"+username+"' is not there, skiping delete...");	

	       }
	       else{
	    	   Thread.sleep(5000);
	    	   System.out.println("User '"+username+"' exists, deleting");
	    	   UsersContainer.Delete.click();
	    	   Thread.sleep(5000);
	    	   TestUtils.assertElementAppears(UsersContainer.DeleteMessage);
	    	   UsersContainer.DeleteButton.click();
	    	   	
	       }
	}
	
	@Then("^Admin creates new user form with the following parameters$")
	public void admin_creates_new_user_form_with_the_following_parameters(DataTable dt) throws Throwable {
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	    TestUtils.assertElementAppears(UsersContainer.AddUserButton);
		UsersContainer.AddUserButton.click();
		
	    List<Map<String, String>> list = dt.asMaps(String.class, String.class);
	    TestUtils.verifiedSendKeys(UsersContainer.Username, list.get(0).get("Username"));
	    TestUtils.verifiedSendKeys(UsersContainer.Email, list.get(0).get("Email"));
	    TestUtils.verifiedSendKeys(UsersContainer.FirstName, list.get(0).get("First Name"));
	    TestUtils.verifiedSendKeys(UsersContainer.LastName, list.get(0).get("Last Name"));
	    
	    TestUtils.assertTextAppears(com.ibm.security.bdd.containers.UsersContainer.UserEnabledON, "ON");
		System.out.println("User Enabled button is ON by default");
		
	    TestUtils.assertTextAppears(com.ibm.security.bdd.containers.UsersContainer.EmailVerifiedOFF, "OFF");
		System.out.println("Email Verified button is OFF by default");
		UsersContainer.Save.click();
		TestUtils.assertTextAppears(UsersContainer.SuccessPopup, "Success! The user has been created.");
		System.out.println("Alert popup: Success! The user has been created is displayed");
		Thread.sleep(5000);
	}
	
	@Then("^search for \"(.*?)\" and delete$")
	public void search_for_and_delete(String username) throws Throwable {
		TestUtils.verifiedSendKeys(UsersContainer.UserSearchText, username );
		UsersContainer.UserSearchButton.click();
		Thread.sleep(3000);
	    UsersContainer.Delete.click();
	    Thread.sleep(3000);
	    TestUtils.assertElementAppears(UsersContainer.DeleteMessage);
	    UsersContainer.DeleteButton.click();
	    TestUtils.assertTextAppears(UsersContainer.SuccessPopup, "Success! The user has been deleted.");
		System.out.println("Alert popup: Success! The user has been deleted.");
		System.out.println("Deleting user...");	
	}
	
	
	@Then("^Verify user \"(.*?)\" deleted successfully$")
	public void verify_user_deleted_successfully(String username) throws Throwable {
		Thread.sleep(3000);
		TestUtils.verifiedSendKeys(UsersContainer.UserSearchText, username );
		UsersContainer.UserSearchButton.click();
		Thread.sleep(3000);
		TestUtils.isElementPresent(By.xpath("//td[contains(text(),'"+username+"')]"));
		System.out.println("User '"+username+"' does not exist. It was deleted successfully");		
	}

}
