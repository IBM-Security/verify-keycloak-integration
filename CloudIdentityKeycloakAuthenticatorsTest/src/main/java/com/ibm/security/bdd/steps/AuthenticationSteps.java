package com.ibm.security.bdd.steps;

import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebDriver;
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
		Select flowDropdown = new Select(AuthenticationContainer.FlowsDropDown);
	    flowDropdown.selectByVisibleText(alias);
	    
	    AuthenticationContainer.DeleteButton.click();
	    Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	    AuthenticationContainer.DeleteFlowButton.click();
	    Thread.sleep(TestUtils.ONE_SECOND_IN_MS);
	    
	    TestUtils.assertElementAppears(AuthenticationContainer.SuccessIcon);
	    TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Flow removed");
	    TestUtils.assertTextNotAppearsInDropDown(AuthenticationContainer.FlowsDropDown, alias);
	}
	
	@Then("^Admin add execution \"(.*?)\" to flow$")
	public void admin_add_execution_to_flow(String execution) throws Throwable {
	    TestUtils.assertElementAppears(AuthenticationContainer.AddExecutionButton);
		AuthenticationContainer.AddExecutionButton.click();
	    
	    Select providerDropdown = new Select(AuthenticationContainer.ProviderDropDown);
	    providerDropdown.selectByVisibleText(execution);
	    AuthenticationContainer.SaveButton.click();
	    
	    TestUtils.assertElementAppears(AuthenticationContainer.SuccessIcon);
	    TestUtils.assertTextAppears(AuthenticationContainer.SuccessMessage, "Success! Execution Created.");
	}
	


}
