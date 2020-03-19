package com.ibm.security.bdd.steps;

import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.ibm.security.bdd.containers.AuthenticationContainer;
import com.ibm.security.bdd.containers.DashboardContainer;
import com.ibm.security.bdd.containers.HomepageContainer;
import com.ibm.security.bdd.containers.LoginContainer;
import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

import io.cucumber.datatable.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class AuthenticationSteps {

	private CommonSteps CommonSteps = new CommonSteps();
	private AuthenticationContainer AuthenticationContainer = new AuthenticationContainer();
	private WebDriver driver = WebDriverFactory.getDriver();
	
	@Then("^Admin creates new Top Level Form with the following parameters$")
	public void admin_creates_new_Top_Level_Form_with_the_following_parameters(DataTable dt) throws Throwable {
		AuthenticationContainer.FlowsTab.click();
	    AuthenticationContainer.NewButton.click();
	    
	    List<Map<String, String>> list = dt.asMaps(String.class, String.class);
	    TestUtils.verifiedSendKeys(AuthenticationContainer.AliasText, list.get(0).get("Alias"));
	    TestUtils.verifiedSendKeys(AuthenticationContainer.DescriptionText, list.get(0).get("Description"));
	    
	    Select flowType = new Select(AuthenticationContainer.FlowTypeDropDown);
	    flowType.selectByVisibleText(list.get(0).get("Flow Type"));
	    
	    AuthenticationContainer.SaveButton.click();		
	}
	
	@Then("^Admin sees new Alias \"(.*?)\" name created on the Flow dropdown$")
	public void admin_sees_new_Alias_name_created_on_the_Flow_dropdown(String alias) throws Throwable {
		TestUtils.assertTextAppearsInDropDown(AuthenticationContainer.FlowsDropDown, alias);
	}
	
	@Then("^Admin selects the Top Level Form \"(.*?)\"$")
	public void admin_selects_the_Top_Level_Form(String alias) throws Throwable {
		Select flowDropdown = new Select(AuthenticationContainer.FlowsDropDown);
	    flowDropdown.selectByVisibleText(alias);
	}

	/* @Then("^Admin verifies the Description \"(.*?)\" tooltip$")
	public void admin_verifies_the_Description_tooltip(String description) throws Throwable {
		AuthenticationContainer.QuestionToolip.click();
		TestUtils.assertTextAppears(AuthenticationContainer.QuestionToolip, description);
	} */

	@Then("^Admin deletes Alias \"(.*?)\" name from the Flow dropdown and verifies successful$")
	public void admin_deletes_Alias_name_from_the_Flow_dropdown_and_verifies_successful(String alias) throws Throwable {
		try {
		
			Select flowDropdown = new Select(AuthenticationContainer.FlowsDropDown);
			flowDropdown.selectByVisibleText(alias);
	    
			AuthenticationContainer.DeleteButton.click();
			Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
			TestUtils.assertTextAppears(AuthenticationContainer.WindowHeader, "Delete Flow");
			AuthenticationContainer.DeleteConfirmButton.click();
			Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	    
			TestUtils.assertElementAppears(AuthenticationContainer.SuccessIcon);
			TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Flow removed");
			TestUtils.assertTextNotAppearsInDropDown(AuthenticationContainer.FlowsDropDown, alias);
	    
		} catch (StaleElementReferenceException e) {
		}
	}
	
	@Then("^Admin add execution \"(.*?)\" to flow$")
	public void admin_add_execution_to_flow(String execution) throws Throwable {
	    TestUtils.assertElementAppears(AuthenticationContainer.AddExecutionButton);
		AuthenticationContainer.AddExecutionButton.click();
	    
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	    Select providerDropdown = new Select(AuthenticationContainer.ProviderDropDown);
	    providerDropdown.selectByVisibleText(execution);
	    AuthenticationContainer.SaveButton.click();
	    
	    Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	    TestUtils.assertElementAppears(AuthenticationContainer.SuccessIcon);
	    TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Execution Created.");
	}
	
	@Then("^Admin deletes Auth Type \"(.*?)\" from flow$")
	public void admin_deletes_execution_from_flow(String execution) throws Throwable {
		try {
		
			List<WebElement> Rows = null;
			List<WebElement> Cols = null;
			WebElement table = AuthenticationContainer.AuthenticationTable;
			String Col = null;
		
			Rows = table.findElements(By.xpath("./tbody/tr"));		
			boolean found = false;
			
			for (int i=1; i<Rows.size(); i++) {
		
				Cols = table.findElements(By.xpath("./tbody/tr["+i+"]/td"));
				Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
			
				if (execution.equals(Col)) {		
					WebElement Cell = table.findElement(By.xpath("./tbody/tr["+i+"]/td["+Cols.size()+"]"));
				
					Cell.findElement(By.xpath("./div/a/b[@class='caret']")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					Cell.findElement(By.xpath("./div/ul/li/a[contains(text(),'Delete')]")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
				
					TestUtils.assertElementAppears(AuthenticationContainer.WindowHeader);
					TestUtils.assertTextAppears(AuthenticationContainer.WindowHeader, "Delete Execution");
					AuthenticationContainer.DeleteConfirmButton.click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					found = true;
					break;
				}
			}
			assertTrue("Auth Type is not found", found);
			Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		
		} catch (StaleElementReferenceException e) {
		}
	}
		
	@Then("^Admin sees Auth Type \"(.*?)\" on the table$")
	public void admin_sees_Auth_Type_on_the_table(String authType) throws Throwable {
		try {
		
			WebElement table = AuthenticationContainer.AuthenticationTable;
			List<WebElement> Rows = table.findElements(By.xpath(".//tbody/tr"));
		
			String Col = null;
			boolean found = false;
		
			for (int i=1; i<Rows.size(); i++) {
			
				Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
				if (authType.equals(Col)) {
					found = true;
					break;
				}
			
			}
			assertTrue("Auth Type is not found", found);
		
		} catch (StaleElementReferenceException e) {
		}
	}
	
	@Then("^Admin sees Auth Type \"(.*?)\" deleted from the table$")
	public void admin_sees_Auth_Type_deleted_from_the_table(String authType) throws Throwable {
		try {
			
			WebElement table = AuthenticationContainer.AuthenticationTable;
			List<WebElement> Rows = table.findElements(By.xpath(".//tbody/tr"));
		
			String Col = null;
			boolean found = false;
		
			for (int i=1; i<Rows.size(); i++) {
				Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
				Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
				if (authType.equals(Col)) {
					found = true;
					break;
				}
			}
			assertFalse("Auth Type appears on the table", found);
			Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		
		} catch (StaleElementReferenceException e) {
		}
	}
		
	@Then("^Admin selects Requirement \"(.*?)\" checkbox for \"(.*?)\" Auth Type$")
	public void admin_selects_Requirement_checkbox_for_Auth_Type(String requirement, String authType) throws Throwable {
		try {
			
			List<WebElement> Rows = null;
			WebElement table = AuthenticationContainer.AuthenticationTable;
			String Col = null;
		
			Rows = table.findElements(By.xpath("./tbody/tr"));		
			boolean found = false;
		
			for (int i=1; i<Rows.size(); i++) {
		
				Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
			
				if (authType.equals(Col)) {		
				
					WebElement Cell = table.findElement(By.xpath("./tbody/tr["+i+"]"));  
					Cell.findElement(By.xpath("./td/label/input[@value='"+requirement+"']")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
				
					found = true;
					break;
				}
			}
			assertTrue("Auth Type is not found", found);
			Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		
		} catch (StaleElementReferenceException e) {
		}
	}
	
	/*@Then("^Admin sees Requirement \"(.*?)\" checkbox is selected for \"(.*?)\" Auth Type$")
	public void admin_sees_Requirement_checkbox_is_selected_for_Auth_Type(String requirement, String authType) throws Throwable {
	   
		List<WebElement> Rows = null;
		WebElement table = AuthenticationContainer.AuthenticationTable;
		String Col = null;
		
		Rows = table.findElements(By.xpath("./tbody/tr"));		
		boolean found = false;
		
		for (int i=1; i<Rows.size(); i++) {
		
			Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
			
			if (authType.equals(Col)) {		
				
				WebElement Cell = table.findElement(By.xpath("./tbody/tr["+i+"]"));  
				Cell.findElement(By.xpath("./td/label/input[@value='"+requirement+"']")).isSelected();
				Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
				
				found = true;
				break;
			}
		}
		assertTrue("Auth Type is not found", found);
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	} */
	
	@Then("^Admin copies the flow using a new name \"(.*?)\"")
	public void admin_copies_the_flow_using_a_new_name(String newFlowName) throws Throwable {
		AuthenticationContainer.CopyButton.click();
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		TestUtils.assertElementAppears(AuthenticationContainer.WindowHeader);
		TestUtils.assertTextAppears(AuthenticationContainer.WindowHeader, "Copy Authentication Flow");
		TestUtils.verifiedSendKeys(AuthenticationContainer.NewNameText, newFlowName);
		AuthenticationContainer.OkButton.click();
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		TestUtils.assertElementAppears(AuthenticationContainer.SuccessIcon);
		TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Flow copied.");
		Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		TestUtils.assertTextAppearsInDropDown(AuthenticationContainer.FlowsDropDown, newFlowName);
	}
	
	@Then("^Admin creates authenticator config for Auth Type \"(.*?)\" with the following parameters$")
	public void admin_creates_authenticator_config_for_Auth_Type_with_the_following_parameters(String authType, DataTable cucumberTable) throws Throwable {
		try {
			List<WebElement> Rows = null;
			List<WebElement> Cols = null;
			WebElement table = AuthenticationContainer.AuthenticationTable;
			String Col = null;
		
			Rows = table.findElements(By.xpath("./tbody/tr"));		
			boolean found = false;
		
			for (int i=1; i<Rows.size(); i++) {
		
				Cols = table.findElements(By.xpath("./tbody/tr["+i+"]/td"));
				Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
			
				if (authType.equals(Col)) {		
					WebElement Cell = table.findElement(By.xpath("./tbody/tr["+i+"]/td["+Cols.size()+"]"));
				
					Cell.findElement(By.xpath("./div/a/b[@class='caret']")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					Cell.findElement(By.xpath("./div/ul/li/a[contains(text(),'Config')]")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
				
					List<Map<String, String>> list = cucumberTable.asMaps(String.class, String.class);
					TestUtils.verifiedSendKeys(AuthenticationContainer.ConfigAliasText, list.get(0).get("Alias"));
					TestUtils.verifiedSendKeys(AuthenticationContainer.TenantNameText, list.get(0).get("Tenant Name"));
					TestUtils.verifiedSendKeys(AuthenticationContainer.APIClientIDText, list.get(0).get("API Client ID"));
					TestUtils.verifiedSendKeys(AuthenticationContainer.APIClientSecretText, list.get(0).get("API Client Secret"));
					AuthenticationContainer.SaveButton.click();
				    
					found = true;
					break;
				}
			}
			assertTrue("Auth Type is not found", found);
			Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		
		} catch (StaleElementReferenceException e) {
		}
	}
	
	
	@Then("^Admin selects Config Action for Auth Type \"(.*?)\" on the table$")
	public void admin_selects_Config_Action_for_Auth_Type_on_the_table(String authType) throws Throwable {
		try {
			
			List<WebElement> Rows = null;
			List<WebElement> Cols = null;
			WebElement table = AuthenticationContainer.AuthenticationTable;
			String Col = null;
		
			Rows = table.findElements(By.xpath("./tbody/tr"));		
			boolean found = false;
		
			for (int i=1; i<Rows.size(); i++) {
		
				Cols = table.findElements(By.xpath("./tbody/tr["+i+"]/td"));
				Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
			
				if (authType.equals(Col)) {		
					WebElement Cell = table.findElement(By.xpath("./tbody/tr["+i+"]/td["+Cols.size()+"]"));
				
					Cell.findElement(By.xpath("./div/a/b[@class='caret']")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					Cell.findElement(By.xpath("./div/ul/li[5]/a[contains(text(),'Config')]")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
			  
					found = true;
					break;
				}
			}
			assertTrue("Auth Type is not found", found);
			Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
		
		} catch (StaleElementReferenceException e) {
		}
	}
	
	@Then("^Admin verifies the Config has the following parameters$")
	public void admin_verifies_the_Config_has_the_following_parameters(DataTable cucumberTable) throws Throwable {
		try {
		
			TestUtils.assertElementAppears(AuthenticationContainer.ConfigDeleteIcon);
	    
			boolean idtext = AuthenticationContainer.IDText.isEnabled();
	    
			if (idtext) {
				System.out.println("it's enabled");
			} else {
				System.out.println("it's disabled");
			}
	    
			AuthenticationContainer.SaveButton.isEnabled();
			String myele = AuthenticationContainer.TenantNameText.getAttribute("readonly");
			System.out.println("element value is " + myele);
	    
			List<Map<String, String>> list = cucumberTable.asMaps(String.class, String.class);
			TestUtils.assertTextAppears(AuthenticationContainer.ConfigHeader, list.get(0).get("Alias"));
			TestUtils.assertTextAppearsInTextfield(AuthenticationContainer.TenantNameText, list.get(0).get("Tenant Name"));
			TestUtils.assertTextAppearsInTextfield(AuthenticationContainer.APIClientIDText, list.get(0).get("API Client ID"));
			TestUtils.assertTextAppearsInTextfield(AuthenticationContainer.APIClientSecretText, list.get(0).get("API Client Secret"));
	   
		} catch (StaleElementReferenceException e) {
		}
	}
		
	
}
