package com.ibm.security.bdd.steps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.ibm.security.bdd.containers.UserAccountContainer;
import io.cucumber.java.en.Then;

public class AddDeleteUsersSteps {
	
	private WebDriver driver = WebDriverFactory.getDriver();
	private UsersContainer UsersContainer = new UsersContainer();
	private CommonContainer CommonContainer = new CommonContainer();
	private UserAccountContainer UserAccountContainer = new UserAccountContainer();
	
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
	    
	    TestUtils.assertTextAppears(UsersContainer.UserEnabledON, "ON");
		System.out.println("User Enabled button is ON by default");
		
	    TestUtils.assertTextAppears(UsersContainer.EmailVerifiedOFF, "OFF");
		System.out.println("Email Verified button is OFF by default");
		UsersContainer.Save.click();
		TestUtils.assertTextAppears(UsersContainer.SuccessPopup, "Success! The user has been created.");
		System.out.println("Alert popup: Success! The user has been created is displayed");
		UsersContainer.ImpersonateButton.click();
		Thread.sleep(3000);

		//Switch to Keycloak Account Management tab		
		String parentWindow = TestUtils.switchToChildTab(driver);
		
		//Set the password for the account created
		UserAccountContainer.UserPassword.click();
		TestUtils.verifiedSendKeys(UserAccountContainer.InputNewPassword, "g0vmware");
		TestUtils.verifiedSendKeys(UserAccountContainer.InputConfirmation, "g0vmware");
		UserAccountContainer.PasswordSave.click();
		System.out.println("Password for user set to g0vmware");
		TestUtils.assertTextAppears(UserAccountContainer.PasswordUpdated, "Your password has been updated.");
		
		//Switch back to Keycloak Admin Console tab
		TestUtils.switchToParentTab(driver, parentWindow);
	}
	
	@Then("^Verify elements on Account panel$")
	public void verify_elements_on_Account_panel() throws Throwable {
		UserAccountContainer.UserAccount.click();
		
		TestUtils.assertElementAppears(UserAccountContainer.EditAccountLabel);
		TestUtils.assertTextAppears(UserAccountContainer.EditAccountLabel, "Edit Account");
		
		TestUtils.assertElementAppears(UserAccountContainer.RequiredFieldsLabel);
		TestUtils.assertTextAppears(UserAccountContainer.RequiredFieldsLabel, "* Required fields");
		
		TestUtils.assertElementAppears(UserAccountContainer.UsernameLabel);
		TestUtils.assertTextAppears(UserAccountContainer.UsernameLabel, "Username");
		TestUtils.assertElementAppears(UserAccountContainer.Username);
		
		Thread.sleep(1000);
		TestUtils.assertElementAppears(UserAccountContainer.EmailLabel);
		TestUtils.assertTextAppears(UserAccountContainer.EmailLabel, "Email");
		TestUtils.assertElementAppears(UserAccountContainer.Email);
		
		TestUtils.assertElementAppears(UserAccountContainer.FirstnameLabel);
		TestUtils.assertTextAppears(UserAccountContainer.FirstnameLabel, "First name");
		TestUtils.assertElementAppears(UserAccountContainer.Firstname);
		
		TestUtils.assertElementAppears(UserAccountContainer.LastnameLabel);
		TestUtils.assertTextAppears(UserAccountContainer.LastnameLabel, "Last name");
		TestUtils.assertElementAppears(UserAccountContainer.Lastname);
				
		TestUtils.assertElementAppears(UserAccountContainer.Cancel);
		TestUtils.assertElementAppears(UserAccountContainer.AccountSave);
	}

	@Then("^Verify elements on Password panel$")
	public void verify_elements_on_Password_panel() throws Throwable{
		UserAccountContainer.UserPassword.click();    
		TestUtils.assertElementAppears(UserAccountContainer.ChangePasswordLabel);
		TestUtils.assertTextAppears(UserAccountContainer.ChangePasswordLabel, "Change Password");
		
		TestUtils.assertElementAppears(UserAccountContainer.Allfieldsrequired);
		TestUtils.assertTextAppears(UserAccountContainer.Allfieldsrequired, "All fields required");
		
		TestUtils.assertElementAppears(UserAccountContainer.Passwordlabel);
		TestUtils.assertTextAppears(UserAccountContainer.Passwordlabel, "Password");
		TestUtils.assertElementAppears(UserAccountContainer.InputPassword);
		
		TestUtils.assertElementAppears(UserAccountContainer.NewPasswordlabel);
		TestUtils.assertTextAppears(UserAccountContainer.NewPasswordlabel, "New Password");
		TestUtils.assertElementAppears(UserAccountContainer.InputNewPassword);
		
		TestUtils.assertElementAppears(UserAccountContainer.Confirmationlabel);
		TestUtils.assertTextAppears(UserAccountContainer.Confirmationlabel, "Confirmation");
		TestUtils.assertElementAppears(UserAccountContainer.InputConfirmation);
		
		TestUtils.assertElementAppears(UserAccountContainer.PasswordSave);
	}

	@Then("^Verify elements on Authenticator panel$")
	public void verify_elements_on_AUthenticator_panel() throws Throwable {
		UserAccountContainer.UserAuthenticator.click();
		TestUtils.assertElementAppears(UserAccountContainer.Authenticatorlabel);
		TestUtils.assertTextAppears(UserAccountContainer.Authenticatorlabel, "Authenticator");
		TestUtils.assertElementAppears(UserAccountContainer.P1text);
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		TestUtils.assertTextAppears(UserAccountContainer.P1text, "Install one of the following applications on your mobile");
		TestUtils.assertTextAppears(UserAccountContainer.FreeOTPtext, "FreeOTP");
		TestUtils.assertTextAppears(UserAccountContainer.GoogleAuthtext, "Google Authenticator");
		TestUtils.assertTextAppears(UserAccountContainer.P2text, "Open the application and scan the barcode");
		TestUtils.assertElementAppears(UserAccountContainer.Img);
		TestUtils.assertTextAppears(UserAccountContainer.P3text, "Enter the one-time code provided by the application and click Save to finish the setup.");
		TestUtils.assertElementAppears(UserAccountContainer.OTClabel);
		TestUtils.assertTextAppears(UserAccountContainer.OTClabel, "One-time code");
		TestUtils.assertElementAppears(UserAccountContainer.DeviceNamelabel);
		TestUtils.assertTextAppears(UserAccountContainer.DeviceNamelabel, "Device Name");
		TestUtils.assertElementAppears(UserAccountContainer.InputOTC);
		TestUtils.assertElementAppears(UserAccountContainer.InputDeviceName);
		TestUtils.assertElementAppears(UserAccountContainer.AuthCancel);
		TestUtils.assertElementAppears(UserAccountContainer.AuthSave);
		//Unable to scan?
		TestUtils.assertElementAppears(UserAccountContainer.Unabletoscanlink);
		TestUtils.assertTextAppears(UserAccountContainer.Unabletoscanlink, "Unable to scan?");
		UserAccountContainer.Unabletoscanlink.click();
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		TestUtils.assertElementAppears(UserAccountContainer.EnterKey);
		TestUtils.assertElementAppears(UserAccountContainer.ScanBarcodelink);
		TestUtils.assertTextAppears(UserAccountContainer.ScanBarcodelink, "Scan barcode?");
		TestUtils.assertTextAppears(UserAccountContainer.P22text, "Open the application and enter the key");
		TestUtils.assertTextAppears(UserAccountContainer.P33text, "Use the following configuration values if the application allows setting them");
		TestUtils.assertTextAppears(UserAccountContainer.P4text, "Enter the one-time code provided by the application and click Save to finish the setup.");
		TestUtils.assertElementAppears(UserAccountContainer.Type);
		TestUtils.assertElementAppears(UserAccountContainer.Algorithm);
		TestUtils.assertElementAppears(UserAccountContainer.Digits);
		TestUtils.assertElementAppears(UserAccountContainer.Interval);
		UserAccountContainer.ScanBarcodelink.click();
	
	}

	@Then("^Verify elements on Sessions$")
	public void verify_elements_on_Sessions() throws Throwable {
		Thread.sleep(1000);
		UserAccountContainer.UserSessions.click();
		TestUtils.assertElementAppears(UserAccountContainer.SessionsLabel);
		TestUtils.assertTextAppears(UserAccountContainer.SessionsLabel, "Sessions");		
		TestUtils.assertElementAppears(UserAccountContainer.LogOutAllSessions);
		TestUtils.assertTextAppears(UserAccountContainer.LogOutAllSessions, "Log out all sessions");		
		
	}

	@Then("^Verify elements on Application$")
	public void verify_elements_on_Application() throws Throwable {
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		UserAccountContainer.UserApplications.click();
		TestUtils.assertElementAppears(UserAccountContainer.ApplicationsLabel);
		TestUtils.assertTextAppears(UserAccountContainer.ApplicationsLabel, "Applications");
		TestUtils.assertElementAppears(UserAccountContainer.AccountLink);
		UserAccountContainer.AccountLink.click();
		TestUtils.assertElementAppears(UserAccountContainer.UserAccount);
		TestUtils.assertElementAppears(UserAccountContainer.EditAccountLabel);
		
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
		System.out.println("User '"+username+"' was deleted successfully");		
	}

}
