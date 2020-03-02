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
	
	//Login dialog when invalid user credentials used
	
	@FindBy(how = How.CSS, using = "div.kc-logo-text")
	public WebElement KeyClockLogos;
	
	@FindBy(how = How.CSS, using = "#kc-page-title")
	public WebElement WeAreSorry;
	
	@FindBy(how = How.CSS, using = ".instruction")
	public WebElement ErrorMessage;
	
	@FindBy(how = How.CSS, using = "#backToApplication")
	public WebElement BackToAppLink;
	
	@FindBy(how = How.CSS, using = "navbar-brand")
	public WebElement KeycloakIcon;
	
	
	// Navigation - Configure
	
	@FindBy(how = How.XPATH, using = "//h2[contains(text(),'Configure')]")
	public WebElement ConfigureLabel;
	
	@FindBy(how = How.LINK_TEXT , using = "Realm Settings")
	public WebElement RealmSettingsLink1;
	
	@FindBy(how = How.LINK_TEXT , using = "Clients")
	public WebElement ClientsLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Client Scopes")
	public WebElement ClientScopesLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Roles")
	public WebElement RolesLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Identity Providers")
	public WebElement IdentityProvidersLink;
	
	@FindBy(how = How.LINK_TEXT , using = "User Federation")
	public WebElement UserFederationLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Authentication")
	public WebElement AuthenticationLink;
	
	//Navigation - Manage
	
	@FindBy(how = How.XPATH, using = "//h2[contains(.,'Manage')]")
	public WebElement ManageLabel;
	
	@FindBy(how = How.LINK_TEXT , using = "Groups")
	public WebElement GroupsLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Users")
	public WebElement UsersLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Sessions")
	public WebElement SessionsLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Events")
	public WebElement EventsLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Import")
	public WebElement ImportLink;
	
	@FindBy(how = How.LINK_TEXT , using = "Export")
	public WebElement ExportLink;
	
	
}
