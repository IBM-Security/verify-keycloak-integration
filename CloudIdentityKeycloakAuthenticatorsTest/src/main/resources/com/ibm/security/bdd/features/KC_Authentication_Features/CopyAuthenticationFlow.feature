@Publish @JT
Feature: Copy Authentication Flow
This feature creates a new authentication flow, copy the flow, and verify the flow is accurately copied. 

Background: Login as Admin to the Admin console and navigate to Authentication
Given "admin" logs into the "Admin Console" and navigate to "Authentication"

Scenario: [1] Admin creates a new flow, add several executions to the flow, and set the requirement on each execution
Then Admin deletes the flow "Cloud Identity Copy1" if it exists
Then Admin deletes the flow "Cloud Identity Copy2" if it exists
Then Admin creates new Top Level Form with the following parameters
| Alias                | Description       | Flow Type |
| Cloud Identity Copy1 | Testing flow copy | generic  |
Then Admin add execution "Username Password Form" to flow
And Admin selects Requirement "REQUIRED" checkbox for "Username Password Form" Auth Type
Then Admin add execution "Cloud Identity Authenticator" to flow
And Admin selects Requirement "REQUIRED" checkbox for "Cloud Identity Authenticator" Auth Type
Then Admin add execution "Cloud Identity QR Login Authenticator" to flow
And Admin selects Requirement "REQUIRED" checkbox for "Cloud Identity QR Login Authenticator" Auth Type

Scenario: [2] Admin copies the flow using a different name, selects the copied flow and verifies the table
Then Admin selects the Top Level Form "Cloud Identity Copy1" 
Then Admin copies the flow using a new name "Cloud Identity Copy2"
And Admin sees Auth Type "Username Password Form" on the table
And Admin sees Auth Type "Cloud Identity Authenticator" on the table
And Admin sees Auth Type "Cloud Identity QR Login Authenticator" on the table

Scenario: [3] Admin deletes the flow and the flow copied 
Then Admin deletes Alias "Cloud Identity Copy1" name from the Flow dropdown and verifies successful
Then Admin deletes Alias "Cloud Identity Copy2" name from the Flow dropdown and verifies successful
