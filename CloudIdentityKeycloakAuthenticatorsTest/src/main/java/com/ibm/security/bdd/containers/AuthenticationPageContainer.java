package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import com.ibm.security.bdd.util.WebDriverFactory;

public class AuthenticationPageContainer {

	public AuthenticationPageContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
	}
	    //Drop down 
		@FindBy(how = How.CSS, using = "select.form-control")
		public WebElement AuthDropDown;
		
		//Drop-down entries
		@FindBy(how = How.CSS, using = "select.form-control option[label='Browser'")
		public WebElement DropDown_Browser;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='Direct Grant'")
		public WebElement DropDown_DirectGrant;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='Registration'")
		public WebElement DropDdown_Registration;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='Reset Credentials'")
		public WebElement DropDown_ResetCredentials;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='Clients'")
		public WebElement DropDown_Clients;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='First Broker Login'")
		public WebElement DropDown_FirstBL;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='Docker Auth'")
		public WebElement DropDown_DockerAuth;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='Http Challenge'")
		public WebElement DropDownHttpC;
		
		@FindBy(how = How.CSS, using = "select.form-control option[label='Copy Of Browser'")
		public WebElement DropDown_CopyOfBrowser;
		
		@FindBy(how = How.CSS, using = "i.fa'")
		public WebElement DropDownInfoButton;
		
		//Tabs:
		//Tab Flows
		@FindBy(how = How.CSS, using = "ul.nav-tabs li[ng-class*='flows']")
		public WebElement TabFlows;
		
		@FindBy(how = How.CSS, using = "div.alert-dismissable']")
		public WebElement AlertPopup;
		
		@FindBy(how = How.CSS, using = "td.kc-sorter']")
		public WebElement FlowsFirstCell;
		
		//Tab Bindings
		@FindBy(how = How.CSS, using = "ul.nav-tabs li[ng-class*='bindings']")
		public WebElement TabBindings;
		
		//Tab Required Actions
		@FindBy(how = How.CSS, using = "ul.nav-tabs li[ng-class*='required']")
		public WebElement TabRequired;
		
		//Tab OTP Policy
		@FindBy(how = How.CSS, using = "ul.nav-tabs li[ng-class*='otp']")
		public WebElement TabOTP;
		
		//Tab WebAuthn Policy
		@FindBy(how = How.CSS, using = "ul.nav-tabs li[ng-class*='webauth']")
		public WebElement TabWebAuth;	
		
		//Buttons
		@FindBy(how = How.CSS, using = "button[data-ng-click*='createFlow()']")
		public WebElement NewButton;
		
		@FindBy(how = How.CSS, using = "button[data-ng-click*='createFlow()']")
		public WebElement CopyButton;
		
		
	}
