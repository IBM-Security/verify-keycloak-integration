package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import com.ibm.security.bdd.util.WebDriverFactory;

public class DashboardContainer {

	public DashboardContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
}
	@FindBy(how = How.CSS, using = ".navbar-brand")
	public WebElement KeycloakIcon;
	
	@FindBy(how = How.LINK_TEXT , using = "Admin")		
	public WebElement AdminDropDown;
	
	@FindBy(how = How.LINK_TEXT , using = "Realm Settings")		
	public WebElement RealmSettings;
	
	@FindBy(how = How.LINK_TEXT , using = "Clients")		
	public WebElement Clients;
	
	@FindBy(how = How.LINK_TEXT , using = "Client Scopes")		
	public WebElement ClientScopes;
	
	@FindBy(how = How.LINK_TEXT , using = "Roles")		
	public WebElement Roles;
	
	@FindBy(how = How.LINK_TEXT , using = "Identity Providers")		
	public WebElement IdentityProviders;
	
	@FindBy(how = How.LINK_TEXT , using = "User Federation")		
	public WebElement UserFederation;
	
	@FindBy(how = How.LINK_TEXT , using = "Authentication")		
	public WebElement Authentication;
	
	@FindBy(how = How.LINK_TEXT , using = "Groups")		
	public WebElement Groups;
	
	@FindBy(how = How.LINK_TEXT , using = "Users")		
	public WebElement Users;
	
	@FindBy(how = How.LINK_TEXT , using = "Sessions")		
	public WebElement Sessions;
	
	@FindBy(how = How.LINK_TEXT , using = "Events")		
	public WebElement Events;
	
	@FindBy(how = How.LINK_TEXT , using = "Import")		
	public WebElement Import;
	
	@FindBy(how = How.LINK_TEXT , using = "Export")		
	public WebElement Export;

}
