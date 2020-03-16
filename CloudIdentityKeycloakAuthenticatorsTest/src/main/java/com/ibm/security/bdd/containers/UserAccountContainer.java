package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.ibm.security.bdd.util.TestUtils;
import com.ibm.security.bdd.util.WebDriverFactory;

public class UserAccountContainer {

	public UserAccountContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
}
	//URL=http://<hostname>:8080/auth/realms/test-realm/account/applications
	
	// User Account Navigation links
	@FindBy(how = How.LINK_TEXT , using = "Account")		
	public WebElement UserAccount;
	@FindBy(how = How.LINK_TEXT , using = "Password")		
	public WebElement UserPassword;
	@FindBy(how = How.LINK_TEXT , using = "Authenticator")		
	public WebElement UserAuthenticator;
	@FindBy(how = How.LINK_TEXT , using = "Sessions")		
	public WebElement UserSessions;
	@FindBy(how = How.LINK_TEXT , using = "Applications")		
	public WebElement UserApplications;
	
	////////ACCOUNT panel//////////////////////////////////////////////////////////////////////////
	
	//Account panel labels
	@FindBy(how = How.CSS, using = "h2")
	public WebElement EditAccountLabel;
	
	@FindBy(how = How.CSS, using = "span.subtitle")
	public WebElement RequiredFieldsLabel;
	
	@FindBy(how = How.CSS, using = "label[for*='username']")
	public WebElement UsernameLabel;
	
	@FindBy(how = How.CSS, using = "label[for*='email']")
	public WebElement EmailLabel;
	
	@FindBy(how = How.CSS, using = "label[for*='firstName']")
	public WebElement FirstnameLabel;
	
	@FindBy(how = How.CSS, using = "label[for*='lastName']")
	public WebElement LastnameLabel;
	
	//Account panel input fields
	@FindBy(how = How.CSS, using = "input#username")
	public WebElement Username;
	
	@FindBy(how = How.CSS, using = "input#email")
	public WebElement Email;
	
	@FindBy(how = How.CSS, using = "input#firstName")
	public WebElement Firstname;
	
	@FindBy(how = How.CSS, using = "input#lastName")
	public WebElement Lastname;
	
	//Account Buttons
	@FindBy(how = How.CSS, using = "button[value*='Cancel']")
	public WebElement Cancel;
	
	@FindBy(how = How.CSS, using = "button[value*='Save']")
	public WebElement AccountSave;
	
	////////PASSWORD panel///////////////////////////////////////////////////////////////
	
	//Password labels
	@FindBy(how = How.XPATH, using ="//h2[contains(text(),'Change Password')]")
	//@FindBy(how = How.CSS, using = "h2")
	public WebElement ChangePasswordLabel;
	
	@FindBy(how = How.CSS, using = "span.subtitle")
	public WebElement Allfieldsrequired;
	
	@FindBy(how = How.CSS, using = "label[for='password']")
	public WebElement Passwordlabel;	
	
	@FindBy(how = How.CSS, using = "label[for='password-new']")
	public WebElement NewPasswordlabel;	
	
	@FindBy(how = How.CSS, using = "label[for='password-confirm']")
	public WebElement Confirmationlabel;
	
	//Password Input fields
	
	@FindBy(how = How.CSS, using = "input#password")
	public WebElement InputPassword;
	
	@FindBy(how = How.CSS, using = "input#password-new")
	public WebElement InputNewPassword;	
	
	@FindBy(how = How.CSS, using = "input#password-confirm")
	public WebElement InputConfirmation;
	
	//Password Save button
	@FindBy(how = How.CSS, using = "button[value*='Save']")
	public WebElement PasswordSave;
	
	
	
	@FindBy(how = How.CSS, using = "span.kc-feedback-text")
	public WebElement PasswordUpdated;
	
	//Popup Alerts
	//Your password has been updated.
	@FindBy(how = How.CSS, using = "div.alert-success")
	public WebElement SuccessAlert;
	
	@FindBy(how = How.XPATH, using ="//span[contains(.,'Invalid existing password.')]")
	public WebElement InvalidPasswordAlert;
	
	@FindBy(how = How.XPATH, using ="//span[contains(.,'Please specify password.')]")
	public WebElement SpecifyPasswordAlert;		
	
	//////AUTHENTICATOR panel//////////////////////////////////////////////////////////////
	
	//Authenticator panel labels
	@FindBy(how = How.XPATH, using ="//h2[contains(text(),'Authenticator')]")
	public WebElement Authenticatorlabel;
	
	@FindBy(how = How.XPATH, using ="//p[contains(.,'Install one of the following applications on your mobile')]")
	public WebElement P1text;
	
	@FindBy(how = How.XPATH, using ="//li[contains(text(),'FreeOTP')]")
	public WebElement FreeOTPtext;
	
	@FindBy(how = How.XPATH, using ="//li[contains(text(),'Google Authenticator')]")
	public WebElement GoogleAuthtext;
	
	@FindBy(how = How.XPATH, using ="//p[contains(.,'Open the application and scan the barcode')]")
	public WebElement P2text;
	
	@FindBy(how = How.CSS, using = "img")
	public WebElement Img;
	
	//Unable to scan? link
	@FindBy(how = How.CSS, using = "#mode-manual")
	public WebElement Unabletoscanlink;
	
	
	@FindBy(how = How.XPATH, using ="//p[contains(.,'Enter the one-time code provided by the application and click Save to finish the setup.')]")
	public WebElement P3text;
	
	//Scan barcode? link
	@FindBy(how = How.CSS, using = "#mode-barcode")
	public WebElement ScanBarcodelink;
	
	@FindBy(how = How.XPATH, using ="//p[contains(text(),'Open the application and enter the key')]")
	public WebElement P22text;
		
	//Open the application and enter the key field
	@FindBy(how = How.CSS, using = "span[id='kc-totp-secret-key']")
	public WebElement EnterKey;
		
	@FindBy(how = How.XPATH, using ="//p[contains(text(),'Use the following configuration values if the appl')]")
	public WebElement P33text;	
		
	@FindBy(how = How.CSS, using = "li#kc-totp-type")
	public WebElement Type;
	
	@FindBy(how = How.CSS, using = "li#kc-totp-algorithm")
	public WebElement Algorithm;
	
	@FindBy(how = How.CSS, using = "li#kc-totp-digits")
	public WebElement Digits;
	
	@FindBy(how = How.CSS, using = "li#kc-totp-period")
	public WebElement Interval;
	
	@FindBy(how = How.XPATH, using ="//p[contains(text(),'Enter the one-time code provided by the applicatio')]")
	public WebElement P4text;
	
	//One-time code label
	@FindBy(how = How.CSS, using = "label[for='totp']")
	public WebElement OTClabel;
		
	//Device Name label
	@FindBy(how = How.CSS, using = "label[for='userLabel']")
	public WebElement DeviceNamelabel;
	
	//Input One-time code
	@FindBy(how = How.CSS, using = "input#totp")
	public WebElement InputOTC;
	
	//Input Device Name
	@FindBy(how = How.CSS, using = "input#userLabel")
	public WebElement InputDeviceName;	
	
	//Authenticator Buttons
	@FindBy(how = How.CSS, using = "button[value*='Cancel']")
	public WebElement AuthCancel;
	
	@FindBy(how = How.CSS, using = "button[value*='Save']")
	public WebElement AuthSave;
	
	////////SESSIONS panel/////////////////////////////////////////////////////////////
	
	@FindBy(how = How.XPATH, using ="//h2[contains(text(),'Sessions')]")
	public WebElement SessionsLabel;
	
	@FindBy(how = How.CSS, using = "button[id='logout-all-sessions']")
	public WebElement LogOutAllSessions;
	
	//////APPLICATIONS panel////////////////////////////////////////////////////////////
	
	@FindBy(how = How.XPATH, using ="//h2[contains(text(),'Applications')]")
	public WebElement ApplicationsLabel;
	
	@FindBy(how = How.CSS, using = "td > a")
	public WebElement AccountLink;
	
}
