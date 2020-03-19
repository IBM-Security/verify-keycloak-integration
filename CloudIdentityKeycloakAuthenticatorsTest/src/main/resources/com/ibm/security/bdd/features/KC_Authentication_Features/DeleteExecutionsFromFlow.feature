@Publish @JT
Feature: Delete Executions from the Flow
This feature add several executions to the flow, find and delete the executions from the table, verify successful, and delete the flow

Background: Login as Admin to the Admin console and navigate to Authentication
Given "admin" logs into the "Admin Console" and navigate to "Authentication"

Scenario: [1] Admin creates a new flow, add several executions to the flow
Then Admin creates new Top Level Form with the following parameters
| Alias                  | Description                       | Flow Type |
| Cloud Identity DE Test | Test deleting execution from flow | generic  |
And Admin add execution "Username Password Form" to flow
And Admin add execution "Cloud Identity Authenticator" to flow
And Admin add execution "Cloud Identity Demo Authenticator" to flow
And Admin add execution "Cloud Identity FIDO Registration Required Action Authenticator" to flow
And Admin add execution "Cloud Identity IBM Verify Registration Required Action Authenticator" to flow
And Admin add execution "Cloud Identity QR Login Authenticator" to flow

Scenario: [2] Admin selects the flow, deletes the executions, and verifies they are removed from the table
Then Admin selects the Top Level Form "Cloud Identity DE Test" 
Then Admin deletes Auth Type "Username Password Form" from flow
And Admin sees Auth Type "Username Password Form" deleted from the table
Then Admin deletes Auth Type "Cloud Identity Authenticator" from flow
And Admin sees Auth Type "Cloud Identity Authenticator" deleted from the table
Then Admin deletes Auth Type "Cloud Identity Demo Authenticator" from flow
And Admin sees Auth Type "Cloud Identity Demo Authenticator" deleted from the table
Then Admin deletes Auth Type "Cloud Identity FIDO Registration Required Action Authenticator" from flow
And Admin sees Auth Type "Cloud Identity FIDO Registration Required Action Authenticator" deleted from the table
Then Admin deletes Auth Type "Cloud Identity IBM Verify Registration Required Action Authenticator" from flow
And Admin sees Auth Type "Cloud Identity IBM Verify Registration Required Action Authenticator" deleted from the table
Then Admin deletes Auth Type "Cloud Identity QR Login Authenticator" from flow
And Admin sees Auth Type "Cloud Identity QR Login Authenticator" deleted from the table

Scenario: [3] Admin deletes the flow and verifies successful
Then Admin deletes Alias "Cloud Identity DE Test" name from the Flow dropdown and verifies successful