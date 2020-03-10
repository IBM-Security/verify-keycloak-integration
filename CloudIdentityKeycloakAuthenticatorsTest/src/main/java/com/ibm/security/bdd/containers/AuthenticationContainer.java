package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import com.ibm.security.bdd.util.WebDriverFactory;

public class AuthenticationContainer {

	public AuthenticationContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
}
		
	@FindBy(how = How.LINK_TEXT , using = "Flows")		
	public WebElement FlowsTab;
	
	@FindBy(how = How.LINK_TEXT , using = "Bindings")		
	public WebElement BindingsTab;
	
	@FindBy(how = How.LINK_TEXT , using = "Required Actions")		
	public WebElement RequiredActionsTab;
	
	@FindBy(how = How.LINK_TEXT , using = "Password Policy")		
	public WebElement PasswordPolicyTab;
	
	@FindBy(how = How.LINK_TEXT , using = "OTP Policy")		
	public WebElement OTPPolicyTab;
	
	@FindBy(how = How.LINK_TEXT , using = "WebAuthn Policy")		
	public WebElement WebAuthnPolicyTab;
	
	// Flows tab
	
	@FindBy(how = How.CSS, using = "button[data-ng-click*='createFlow()']")
	public WebElement NewButton;
	
	@FindBy(how = How.CSS, using = "button[data-ng-click*='copyFlow()']")
	public WebElement CopyButton;	
	
	@FindBy(how = How.CSS, using = "button[data-ng-click*='deleteFlow()']")
	public WebElement DeleteButton;
	
	@FindBy(how = How.CSS, using = "button[data-ng-click*='addExecution()']")
	public WebElement AddExecutionButton;
	
	@FindBy(how = How.CSS, using = "button[data-ng-click*='addFlow()']")
	public WebElement AddFlowButton;
	
	@FindBy(how = How.CSS, using = ".form-control")
	public WebElement FlowsDropDown;
	
	@FindBy(how = How.CSS, using = ".table.table-striped.table-bordered")
	public WebElement AuthenticationTable;
	
	@FindBy(how = How.CSS, using = ".fa-question-circle")
	public WebElement QuestionToolip;
	
	@FindBy(how = How.CSS, using = "button[ng-click*='ok()']")
	public WebElement DeleteFlowButton;
	
	@FindBy(how = How.CSS, using = "button[ng-click*='cancel()']")
	public WebElement CancelFlowButton;
	
	@FindBy(how = How.CSS, using = ".alert-success")
	public WebElement DeleteFlowSuccessMessage;
	
	@FindBy(how = How.CSS, using = ".pficon-ok")
	public WebElement DeleteFlowSuccessIcon;
	
	
	// Create Top Level Form
	
	@FindBy(how = How.CSS, using = "#alias")
	public WebElement AliasText;
	
	@FindBy(how = How.CSS, using = "#description")
	public WebElement DescriptionText;
	
	@FindBy(how = How.CSS, using = "#flowType")
	public WebElement FlowTypeDropDown;
	
	@FindBy(how = How.CSS, using = "button[type*='submit']")
	public WebElement SaveButton;
	
	@FindBy(how = How.CSS, using = "button[ng-click*='cancel()']")
	public WebElement CancelButton;

}
