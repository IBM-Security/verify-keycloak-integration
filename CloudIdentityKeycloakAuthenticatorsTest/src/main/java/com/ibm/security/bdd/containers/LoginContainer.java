
package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import com.ibm.security.bdd.util.WebDriverFactory;

public class LoginContainer {
	
	public LoginContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
	}
	
	@FindBy(how = How.CSS, using = "#username")
	public WebElement UsernameText;
	
	@FindBy(how = How.CSS, using = "#password")
	public WebElement PasswordText;
	
	@FindBy(how = How.CSS, using = "#kc-login")
	public WebElement LoginButton;
	
	@FindBy(how = How.CSS, using = ".kc-logo-text")
	public WebElement KeycloakIcon;
	
	@FindBy(how = How.CSS, using = ".form-group:nth-child(1) .control-label")
	public WebElement UsernameLabel;
	
	@FindBy(how = How.CSS, using = ".form-group+ .form-group .control-label")
	public WebElement PasswordLabel;
	
	@FindBy(how = How.CSS, using = ".pficon-error-circle-o")
	public WebElement ErrorIcon;
	
	@FindBy(how = How.CSS, using = ".kc-feedback-text")
	public WebElement ErrorMessage;
	
	@FindBy(how = How.CSS, using = "#kc-page-title")
	public WebElement LoginTitle;
}