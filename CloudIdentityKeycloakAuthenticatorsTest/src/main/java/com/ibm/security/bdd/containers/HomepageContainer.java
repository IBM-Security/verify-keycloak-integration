package com.ibm.security.bdd.containers;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

import com.ibm.security.bdd.util.WebDriverFactory;

public class HomepageContainer {

	public HomepageContainer() {
		PageFactory.initElements(WebDriverFactory.getDriver(), this);
	}

	@FindBy(how = How.CSS, using = "img[alt=Keycloak]")
	public WebElement KeycloakIcon;
			
	@FindBy(how = How.CSS, using = "h1")
	public WebElement WelcomeToKCMessage;
	
	@FindBy(how = How.CSS, using = ".welcome-primary-link a")
	public WebElement AdminConsoleLink;
	
	@FindBy(how = How.CSS, using = ".welcome-primary-link .description")
	public WebElement AdminConsoleDescription;
	
	@FindBy(how = How.CSS, using = ".h-l > h3 > a")
	public WebElement DocumentationLink;
	
	@FindBy(how = How.CSS, using = ".card-pf > .description")
	public WebElement DocumentationDescription;
	
	@FindBy(how = How.CSS, using = ".h-m:nth-child(1) a")
	public WebElement KeycloakProjectLink;
	
	@FindBy(how = How.CSS, using = ".card-pf:nth-child(2) a")
	public WebElement MailingListLink;
	
	@FindBy(how = How.CSS, using = ".card-pf:nth-child(3) a")
	public WebElement ReportAnIssueLink;
	
}
