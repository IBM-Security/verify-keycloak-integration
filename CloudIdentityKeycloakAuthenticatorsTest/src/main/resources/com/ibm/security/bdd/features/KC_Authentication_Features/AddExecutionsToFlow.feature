@Publish @JT
Feature: Add Executions to the Flow
This feature add several executions to the flow, verify from the table, and delete the flow

Background: Login as Admin to the Admin console and navigate to Authentication
Given "admin" logs into the "Admin Console" and navigate to "Authentication"

Scenario: [1] Admin creates a new flow, add several executions to the flow
Then Admin creates new Top Level Form with the following parameters
| Alias               | Description                   | Flow Type |
| Cloud Identity AE Test | Test adding execution to flow | generic  |
And Admin add execution "Username Password Form" to flow
And Admin add execution "Cloud Identity Authenticator" to flow
And Admin add execution "Cloud Identity Demo Authenticator" to flow
And Admin add execution "Cloud Identity FIDO Registration Required Action Authenticator" to flow
And Admin add execution "Cloud Identity IBM Verify Registration Required Action Authenticator" to flow
And Admin add execution "Cloud Identity QR Login Authenticator" to flow

Scenario: [2] Admin selects the flow, verifies the added executions are found in the table
Then Admin selects the Top Level Form "Cloud Identity AE Test" 
And Admin sees Auth Type "Username Password Form" on the table
And Admin sees Auth Type "Cloud Identity Authenticator" on the table
And Admin sees Auth Type "Cloud Identity Demo Authenticator" on the table
And Admin sees Auth Type "Cloud Identity FIDO Registration Required Action Authenticator" on the table
And Admin sees Auth Type "Cloud Identity IBM Verify Registration Required Action Authenticator" on the table
And Admin sees Auth Type "Cloud Identity QR Login Authenticator" on the table

Scenario: [3] Admin deletes the flow and verifies successful
Then Admin deletes Alias "Cloud Identity AE Test" name from the Flow dropdown and verifies successful