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
	@FindBy(how = How.CSS, using = ".navbar-brand")
	public WebElement KeycloakIcon;
	
	@FindBy(how = How.LINK_TEXT , using = "Admin")		
	public WebElement AdminDropDown;
	

}
