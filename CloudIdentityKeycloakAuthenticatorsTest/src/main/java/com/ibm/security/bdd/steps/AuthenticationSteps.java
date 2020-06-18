package com.ibm.security.bdd.steps;

import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
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

	private static final String Driver = null;
	private CommonSteps CommonSteps = new CommonSteps();
	private AuthenticationContainer AuthenticationContainer = new AuthenticationContainer();
	private WebDriver driver = WebDriverFactory.getDriver();
	private WebDriverWait wait = new WebDriverWait(driver, 5);
	
	@Then("^Admin deletes the flow \"(.*?)\" if it exists$")
	public void admin_deletes_the_flow_if_it_exists(String flow) throws Throwable {
		try {
			
			Select select = new Select(AuthenticationContainer.FlowsDropDown);
			List<WebElement> listOfElements = select.getOptions();
			
			for (WebElement ele:listOfElements) {
				if (flow.equals(ele.getText())) {
					admin_deletes_Alias_name_from_the_Flow_dropdown_and_verifies_successful(flow);
					break;
				}
			}
			
		} catch (StaleElementReferenceException e) {
		}
	}
	
	@Then("^Admin creates new Top Level Form with the following parameters$")
	public void admin_creates_new_Top_Level_Form_with_the_following_parameters(DataTable dt) throws Throwable {
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.FlowsTab));
		AuthenticationContainer.FlowsTab.click();
	    AuthenticationContainer.NewButton.click();
	    
	    wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.AliasText));
	    List<Map<String, String>> list = dt.asMaps(String.class, String.class);
	    TestUtils.verifiedSendKeys(AuthenticationContainer.AliasText, list.get(0).get("Alias"));
	    TestUtils.verifiedSendKeys(AuthenticationContainer.DescriptionText, list.get(0).get("Description"));
	    
	    Select flowType = new Select(AuthenticationContainer.FlowTypeDropDown);
	    flowType.selectByVisibleText(list.get(0).get("Flow Type"));
	    AuthenticationContainer.SaveButton.click();		
	}
	
	@Then("^Admin sees new Alias \"(.*?)\" name created on the Flow dropdown$")
	public void admin_sees_new_Alias_name_created_on_the_Flow_dropdown(String alias) throws Throwable {
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.FlowsDropDown));
		TestUtils.assertTextAppearsInDropDown(AuthenticationContainer.FlowsDropDown, alias);
	}
	
	@Then("^Admin selects the Top Level Form \"(.*?)\"$")
	public void admin_selects_the_Top_Level_Form(String alias) throws Throwable {
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.FlowsDropDown));
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
			wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.FlowsDropDown));
			Select flowDropdown = new Select(AuthenticationContainer.FlowsDropDown);
			flowDropdown.selectByVisibleText(alias);
	    
			wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.DeleteButton));
			AuthenticationContainer.DeleteButton.click();
			wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.WindowHeader));
			TestUtils.assertTextAppears(AuthenticationContainer.WindowHeader, "Delete Flow");
			AuthenticationContainer.DeleteConfirmButton.click();
			
			wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.SuccessIcon));
			TestUtils.assertElementAppears(AuthenticationContainer.SuccessIcon);
			TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Flow removed");
			TestUtils.assertTextNotAppearsInDropDown(AuthenticationContainer.FlowsDropDown, alias);
	    
		} catch (StaleElementReferenceException e) {
		}
	}
	
	@Then("^Admin add execution \"(.*?)\" to flow$")
	public void admin_add_execution_to_flow(String execution) throws Throwable {
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.AddExecutionButton));
		AuthenticationContainer.AddExecutionButton.click();
	    
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.ProviderDropDown));
	    Select providerDropdown = new Select(AuthenticationContainer.ProviderDropDown);
	    providerDropdown.selectByVisibleText(execution);
	    AuthenticationContainer.SaveButton.click();
	    
	    wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.SuccessIcon));
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
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
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
				Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
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
				Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
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
				Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
				Col = table.findElement(By.xpath(".//tr["+i+"]/td[1]/span[contains(@class,'ng-binding')]")).getText();
			
				if (authType.equals(Col)) {		
					
					WebElement Cell = table.findElement(By.xpath("./tbody/tr["+i+"]"));  
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
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
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.CopyButton));
		AuthenticationContainer.CopyButton.click();
		
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.WindowHeader));
		TestUtils.assertElementAppears(AuthenticationContainer.WindowHeader);
		TestUtils.assertTextAppears(AuthenticationContainer.WindowHeader, "Copy Authentication Flow");
		TestUtils.verifiedSendKeys(AuthenticationContainer.NewNameText, newFlowName);
		AuthenticationContainer.OkButton.click();
		
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.SuccessIcon));
		TestUtils.assertElementAppears(AuthenticationContainer.SuccessIcon);
		TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Flow copied.");
		
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.FlowsDropDown));
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
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					Cell.findElement(By.xpath("./div/a/b[@class='caret']")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					Cell.findElement(By.xpath("./div/ul/li/a[contains(text(),'Config')]")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					
					wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.ConfigAliasText));
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
	
	@Then("Admin edits the Config with the following parameters")
	public void admin_edits_the_Config_with_the_following_parameters(DataTable cucumberTable) throws Throwable {
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.IDText));	
		
		List<Map<String, String>> list = cucumberTable.asMaps(String.class, String.class);
		TestUtils.verifiedSendKeys(AuthenticationContainer.TenantNameText, list.get(0).get("Tenant Name"));
		TestUtils.verifiedSendKeys(AuthenticationContainer.APIClientIDText, list.get(0).get("API Client ID"));
		TestUtils.verifiedSendKeys(AuthenticationContainer.APIClientSecretText, list.get(0).get("API Client Secret"));
		//wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.ConfigSaveButton));	
		AuthenticationContainer.ConfigSaveButton.click();
		
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.SuccessIcon));
		TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Your changes have been saved.");
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
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
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
	
	@Then("^Admin selects Config Action for Auth Type \"(.*?)\" and verify textfields are empty$")
	public void admin_selects_Config_Action_for_Auth_Type_and_verify_textfields_are_empty(String authType) throws Throwable {
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
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					Cell.findElement(By.xpath("./div/a/b[@class='caret']")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
					Cell.findElement(By.xpath("./div/ul/li/a[contains(text(),'Config')]")).click();
					Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
			  
					found = true;
					break;
				}
			}
			assertTrue("Auth Type is not found", found);
						
			wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.CreateConfigHeader));
			TestUtils.assertTextAppears(AuthenticationContainer.CreateConfigHeader, "Create authenticator config");
			AuthenticationContainer.ConfigAliasText.getAttribute("value").isEmpty();
			AuthenticationContainer.TenantNameText.getAttribute("value").isEmpty();
			AuthenticationContainer.APIClientIDText.getAttribute("value").isEmpty();
			AuthenticationContainer.APIClientSecretText.getAttribute("value").isEmpty();
		
		} catch (StaleElementReferenceException e) {
		}
	}
		
	@Then("^Admin verifies the Config has the following parameters$")
	public void admin_verifies_the_Config_has_the_following_parameters(DataTable cucumberTable) throws Throwable {
		try {
			wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.ConfigAliasText));
			
			List<Map<String, String>> list = cucumberTable.asMaps(String.class, String.class);
			TestUtils.assertTextAppears(AuthenticationContainer.ConfigHeader, list.get(0).get("Alias"));
			TestUtils.assertTextAppearsInTextfield(AuthenticationContainer.ConfigAliasText, list.get(0).get("Alias"));
			TestUtils.assertTextAppearsInTextfield(AuthenticationContainer.TenantNameText, list.get(0).get("Tenant Name"));
			TestUtils.assertTextAppearsInTextfield(AuthenticationContainer.APIClientIDText, list.get(0).get("API Client ID"));
			TestUtils.assertTextAppearsInTextfield(AuthenticationContainer.APIClientSecretText, list.get(0).get("API Client Secret"));
	   
		} catch (StaleElementReferenceException e) {
		}
	}
	
	
	@Then("^Admin deletes the Config$")
	public void admin_deletes_the_Config() throws Throwable {
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.ConfigDeleteIcon));
		AuthenticationContainer.ConfigDeleteIcon.click();
		
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.WindowHeader));
		TestUtils.assertTextAppears(AuthenticationContainer.WindowHeader, "Delete Config");
		AuthenticationContainer.DeleteConfirmButton.click();
		
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.SuccessIcon));
		TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! The config has been deleted.");
	}
	
	@Then("Admin cancels out of the Config panel")
	public void admin_cancels_out_of_the_Config_panel() throws Throwable {
		wait.until(ExpectedConditions.visibilityOf(AuthenticationContainer.ConfigCancelButton));
		AuthenticationContainer.ConfigCancelButton.click();
	}
		
	
}
