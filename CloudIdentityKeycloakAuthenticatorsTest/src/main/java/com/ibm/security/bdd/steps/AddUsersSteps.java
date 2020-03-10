package com.ibm.security.bdd.steps;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.ibm.security.bdd.containers.UsersContainer;
import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.java.en.Then;

public class AddUsersSteps {

	private WebDriver driver = WebDriverFactory.getDriver();
	private UsersContainer UsersContainer = new UsersContainer();
	
	@Then("^Customer deletes user if exists$")
	public void customer_deletes_user_if_exists() throws Throwable {
		Thread.sleep(3000);
		TestUtils.assertElementAppears(UsersContainer.viewAllUsersButton);
		driver.findElement(By.cssSelector("button#viewAllUsers")).click();
		Thread.sleep(5000);
		
       List<WebElement> usernameExists = driver.findElements(By.xpath("//td[contains(text(),'vberengu@us.ibm.com')]"));
       if(usernameExists.size() == 0){
			System.out.println("User vberengu is not there, skiping delete...");	

       }
       else{
    	   Thread.sleep(5000);
    	   System.out.println("User vberengu exists, deleting");
    	   UsersContainer.Deletevberengu.click();
    	   Thread.sleep(5000);
    	   TestUtils.assertElementAppears(UsersContainer.DeleteMessage);
    	   UsersContainer.DeleteButton.click();
    	   	
       }
	}
	
	@Then("^Customer enters Username like \"(.*?)\"$")
	public void customer_enters_Username_like (String username) throws Throwable {
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		TestUtils.verifiedSendKeys(UsersContainer.Username, username);
	}

	@Then("^Customer enters email like \"(.*?)\"$")
	public void customer_enters_email_like(String email) throws Throwable {
		TestUtils.verifiedSendKeys(UsersContainer.Email, email);  
	}

	@Then("^Customer enters First Name like \"(.*?)\"$")
	public void customer_enters_First_Name_like(String firstname) throws Throwable {
		TestUtils.verifiedSendKeys(UsersContainer.FirstName, firstname);
	}

	@Then("^Customer enters Last Name like \"(.*?)\"$")
	public void customer_enters_Last_Name_like (String lastname) throws Throwable {
		TestUtils.verifiedSendKeys(UsersContainer.LastName, lastname);
	}
	
	@Then("^Customer verifies User Enabled switch is ON$")
	public void customer_verifies_User_Enabled_switch_is_ON() throws Throwable {
		TestUtils.assertTextAppears(UsersContainer.UserEnabledON, "ON");
		System.out.println("User Enabled button is ON by default");
	}
	
	@Then("^Customer verifies Email Verified switch is OFF$")
	public void customer_verifies_Email_Verified_switch_is_OFF() throws Throwable {
		TestUtils.assertTextAppears(UsersContainer.EmailVerifiedOFF, "OFF");
		System.out.println("Email Verified button is OFF by default");
	}
	
	@Then("^Customer clicks Save button to save new user$")
	public void customer_clicks_Save_button_to_save_new_user() throws Throwable {
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		UsersContainer.Save.click();
		TestUtils.assertTextAppears(UsersContainer.SuccessPopup, "Success! The user has been created.");
		System.out.println("Alert popup: Success! The user has been created is displayed");
		Thread.sleep(5000);
	}
	
	@Then("^Customer sets the password for new user$")
	public void customer_sets_the_password_for_new_user() throws Throwable {
	   
	}

	@Then("^Customer verifies that user added successfully$")
	public void customer_verifies_that_user_added_successfully() throws Throwable {
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		TestUtils.assertElementAppears(UsersContainer.viewAllUsersButton);
		UsersContainer.viewAllUsersButton.click();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		TestUtils.assertElementAppears(UsersContainer.Uservberengu);
		System.out.println("Customer verifies that user added successfully");
	}
	

	@Then("^Customer deletes user$")
	public void cuctomer_deletes_user() throws Throwable {
		Thread.sleep(5000);
		driver.findElement(By.cssSelector("button#viewAllUsers")).click();
		Thread.sleep(5000);
		UsersContainer.Deletevberengu.click();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		TestUtils.assertElementAppears(UsersContainer.DeleteMessage);
		UsersContainer.DeleteButton.click();
		TestUtils.assertTextAppears(UsersContainer.SuccessPopup, "Success! The user has been deleted.");
		System.out.println("Alert popup: Success! The user has been deleted.");
		System.out.println("Deleting user...");	
		
	}
	
	@Then("^Customer verifies user deleted successfully$")
	public void customer_verifies_user_deleted_successfully() throws Throwable {
		Thread.sleep(5000);
		TestUtils.isElementPresent(By.xpath("//td[contains(text(),'vberengu@us.ibm.com')]"));
		System.out.println("User vberengu does not exist");	
	}

	
	
}

