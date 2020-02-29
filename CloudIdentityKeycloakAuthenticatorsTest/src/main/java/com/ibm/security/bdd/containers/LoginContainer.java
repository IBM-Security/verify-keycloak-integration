
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
	@FindBy(how = How.CSS, using = ".kc-logo-text")
	public WebElement UsernameLabel;
}