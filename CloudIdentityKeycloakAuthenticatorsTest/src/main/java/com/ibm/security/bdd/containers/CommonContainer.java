package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.ibm.security.bdd.util.WebDriverFactory;

public class CommonContainer {
	
	public CommonContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
	}

	/**
	 * Some element
	 */
	@FindBy(how = How.CSS, using = "#some-id")
	public WebElement someElement;
	
	@FindBy(how = How.CSS, using = "#user-name-input")
	public WebElement usernameInput;
	
	@FindBy(how = How.CSS, using = "#password-input")
	public WebElement passwordInput;
	
	@FindBy(how = How.CSS, using = "#login-button")
	public WebElement loginButton;
	
	//Users Navigation
	@FindBy(how = How.LINK_TEXT , using = "Users")
	public WebElement UsersLink;
	//xpath=//a[contains(text(),'Users')]
	//.nav-category+ .nav-category li:nth-child(2) .ng-binding

}
