@Publish @JT
Feature: Create Authenticator Config
This feature creates a new flow, add some executions to the flow, and configure the executions.

Background: Login as Admin to the Admin console and navigate to Authentication
Given "admin" logs into the "Admin Console" and navigate to "Authentication"

Scenario: [1] Admin creates a new flow, add an execution to the flow
Then Admin deletes the flow "Cloud Identity Config" if it exists
Then Admin creates new Top Level Form with the following parameters
	| Alias                 | Description           | Flow Type |
	| Cloud Identity Config | Test execution config | generic   |
And Admin add execution "Cloud Identity Authenticator" to flow

Scenario: [2] Admin setup the config for the added execution
Then Admin selects the Top Level Form "Cloud Identity Config" 
Then Admin creates authenticator config for Auth Type "Cloud Identity Authenticator" with the following parameters
	| Alias         | Tenant Name         | API Client ID | API Client Secret |
	| Auto-keycloak | kcuser.keycloak.com | abcd1234      | abcd1234-secret   |

Scenario: [3] Admin verifies the Config has been setup correctly and also edits the Config with new values
Then Admin selects the Top Level Form "Cloud Identity Config" 
Then Admin sees Auth Type "Cloud Identity Authenticator(Auto-keycloak)" on the table
And Admin selects Config Action for Auth Type "Cloud Identity Authenticator(Auto-keycloak)" on the table
Then Admin verifies the Config has the following parameters
  | Alias         | Tenant Name         | API Client ID | API Client Secret |
  | Auto-keycloak | kcuser.keycloak.com | abcd1234      | abcd1234-secret   |
Then Admin edits the Config with the following parameters
 	| Tenant Name             | API Client ID | API Client Secret   |
  | kcuser.keycloak.ibm.com | abcd1234-ibm  | abcd1234-secret-ibm |
     
Scenario: [4] Admin verifies the edit Config was successful and deletes the Config
Then Admin selects the Top Level Form "Cloud Identity Config" 
And Admin selects Config Action for Auth Type "Cloud Identity Authenticator(Auto-keycloak)" on the table
Then Admin verifies the Config has the following parameters
  | Alias         | Tenant Name             | API Client ID     | API Client Secret   |
  | Auto-keycloak | kcuser.keycloak.ibm.com | abcd1234-ibm      | abcd1234-secret-ibm |
Then Admin deletes the Config

Scenario: [4] Admin verifies the delete Config was successful and deletes the flow
Then Admin selects the Top Level Form "Cloud Identity Config" 
And Admin selects Config Action for Auth Type "Cloud Identity Authenticator" and verify textfields are empty
Then Admin cancels out of the Config panel
Then Admin deletes Alias "Cloud Identity Config" name from the Flow dropdown and verifies successful