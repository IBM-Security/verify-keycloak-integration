package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import com.ibm.security.bdd.util.WebDriverFactory;

public class UsersContainer {

	public UsersContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
}
	
	@FindBy(how = How.CSS, using = "h1.ng-binding")
	public WebElement UsersTitle;
	
	@FindBy(how = How.LINK_TEXT , using = "Lookup")		
	public WebElement LookupTab;
	
	@FindBy(how = How.CSS, using = ".form-control")
	public WebElement UserSearchText;	
	
	@FindBy(how = How.CSS, using = "#userSearch")
	public WebElement UserSearchButton;
	
	@FindBy(how = How.CSS, using = "td[data-ng-show*='!users']")
	public WebElement NoResults;
	
	@FindBy(how = How.CSS, using = "button#viewAllUsers")
	public WebElement viewAllUsersButton;
	
	@FindBy(how = How.CSS, using = "button[data-ng-click*='unlockUsers']")
	public WebElement UnlockUsersButton;		
			
	@FindBy(how = How.CSS, using = "a#createUser")
	public WebElement AddUserButton;
	
	//Add user page
	
	@FindBy(how = How.CSS, using = "h1[data-ng-show*='create']")
	public WebElement AdduserTitle;
	
	//ID
	@FindBy(how = How.CSS, using = "#id")
	public WebElement ID;
	
	@FindBy(how = How.XPATH, using = "//label[contains(text(),'ID')]")
	public WebElement IDLabel;
	
	
	@FindBy(how = How.XPATH, using = "//label[contains(text(),'Created At')]")
	public WebElement CreatedAtLabel;
	
	//User Name
	@FindBy(how = How.CSS, using = "label[for*='username']")
	public WebElement UsernameLabel;
	
	@FindBy(how = How.CSS, using = "#username")
	public WebElement Username;
	
	//Email
	@FindBy(how = How.CSS, using = "label[for*='email']")
	public WebElement EmailLabel;
	
	@FindBy(how = How.CSS, using = "#email")
	public WebElement Email;
	
	//First Name
	@FindBy(how = How.CSS, using = "label[for*='firstName']")
	public WebElement FirstNameLabel;
	
	@FindBy(how = How.CSS, using = "#firstName")
	public WebElement FirstName;
	
	//Last Name
	@FindBy(how = How.CSS, using = "label[for*='lastName']")
	public WebElement LastNameLabel;
	
	@FindBy(how = How.CSS, using = "#lastName")
	public WebElement LastName;
	
	//User Enabled ON
	@FindBy(how = How.CSS, using = "label[for*='userEnabled']")
	public WebElement UserEnabledLabel;
	
	@FindBy(how = How.XPATH, using = "//span[@class='ng-isolate-scope ng-not-empty ng-valid']//span[@class='onoffswitch-active ng-binding'][contains(text(),'ON')]")
	public WebElement UserEnabledON;
	
	//User Enabled OFF
	@FindBy(how = How.XPATH, using = "//span[@class='ng-isolate-scope ng-not-empty ng-valid']//span[@class='onoffswitch-inactive ng-binding'][contains(text(),'OFF')]")
	public WebElement UserEnabledOFF;		
	
	//Email Verified OFF
	@FindBy(how = How.CSS, using = "label[for*='emailVerified']")
	public WebElement EmailVerifiedLabel;
	
	@FindBy(how = How.XPATH, using = "//label[@for='emailVerified']//span[contains(@class,'onoffswitch-inactive')][contains(text(),'OFF')]")
	public WebElement EmailVerifiedOFF;
	
	//Email Verified ON
	@FindBy(how = How.XPATH, using = "//span[@class='ng-isolate-scope ng-valid ng-dirty ng-not-empty']//span[@class='onoffswitch-active ng-binding'][contains(text(),'ON')]")
	public WebElement EmailVerifiedON;
	
	//Required User Actions
	
	@FindBy(how = How.CSS, using = "label[for*='reqActions']")
	public WebElement ReqUserActionLabel;
	
	@FindBy(how = How.CSS, using = "#s2id_reqActions")
	public WebElement RequiredUserAction;
	
	//Select Configure OTP from drop down
	@FindBy(how = How.CSS, using = "#select2-result-label-2")
	public WebElement ConfigureOTP;
	
	//Select Update Password from drop down
	@FindBy(how = How.CSS, using = "#select2-result-label-3")
	public WebElement UpdatePassword;
	
	//Select Update Profile from drop down
	@FindBy(how = How.CSS, using = "#select2-result-label-4")
	public WebElement UpdateProfile;
	
	//Select Verify Email from drop down
	@FindBy(how = How.CSS, using = "#select2-result-label-5")
	public WebElement VerifyEmail;
	
	//Save button
	@FindBy(how = How.CSS, using = "button[data-ng-show*='changed']")
	public WebElement Save;
	
	//Cancel button
	@FindBy(how = How.CSS, using = "button[data-ng-click*='cancel()']")
	public WebElement Cancel;
	
	//User name - vberengu@us.ibm.com
	@FindBy(how = How.XPATH, using = "//td[contains(text(),'vberengu@us.ibm.com')]")
	public WebElement Uservberengu;
	
	// Delete user vberengu@us.ibm.com
	@FindBy(how = How.XPATH, using = "//td[contains(text(),'vberengu@us.ibm.com')]/../td[contains(text(),'Delete')]")
	public WebElement Deletevberengu;
	
	//ID for vberengu@us.ibm.com
	@FindBy(how = How.XPATH, using = "//td[contains(text(),'vberengu@us.ibm.com')]/../td/a]")
	public WebElement IDvberengu;
	
	@FindBy(how = How.CSS, using = "div.alert-dismissable")
	public WebElement SuccessPopup;
	
	@FindBy(how = How.CSS, using = "div.alert-danger")
	public WebElement ErrorPopup;
	
	
	
	//////Delete Confirmation dialog/////
	
	@FindBy(how = How.CSS, using = "h4.modal-title")
	public WebElement DeleteUserTitle;
	
	@FindBy(how = How.CSS, using = "h4.modal-title")
	public WebElement Xbutton;
	
	@FindBy(how = How.CSS, using = "div.modal-body")
	public WebElement DeleteMessage;
	
	@FindBy(how = How.CSS, using = "button[data-ng-class*='btns.cancel.cssClass']")
	public WebElement CancelButton;
	
	@FindBy(how = How.CSS, using = "button[data-ng-class*='btns.ok.cssClass']")
	public WebElement DeleteButton;
	

	
}
