@Jason
Feature: Keycloak Homepage Verification
	This feature verifies the elements appearing on the Keycloak Homepage

Scenario: Verify the main elements on Keycloak Homepage are displayed
Given Customer navigate to the Keycloak Homepage
Then Customer verifies the homepage displays Keycloak icon
Then Customer verifies the homepage displays Welcome to Keycloak message
Then Customer verifies the homepage displays Administration Console hyperlink
Then Customer verifies the homepage displays Administration Console Description
Then Customer verifies the homepage displays Documentation hyperlink
Then Customer verifies the homepage displays Documentation Description
Then Customer verifies the homepage displays Keycloak Project hyperlink
Then Customer verifies the homepage displays Mailing List hyperlink
Then Customer verifies the homepage displays Report an issue hyperlink

