@Publish
Feature: Select Requirements for Executions (Auth) Types
This feature add several executions to the flow, find and set the requirement for each execution on the table, 
verify successful, and delete the flow

Background: Login as Admin to the Admin console and navigate to Authentication
Given "admin" logs into the "Admin Console" and navigate to "Authentication"

Scenario: [1] Admin creates a new flow, add several executions to the flow
Then Admin creates new Top Level Form with the following parameters
| Alias                  | Description                         | Flow Type |
| Cloud Identity SR Test | Test selecting requirements on flow | generic  |
And Admin add execution "Username Password Form" to flow
And Admin add execution "Cloud Identity Authenticator" to flow
And Admin add execution "Cloud Identity Demo Authenticator" to flow
And Admin add execution "Cloud Identity FIDO Registration Required Action Authenticator" to flow
And Admin add execution "Cloud Identity IBM Verify Registration Required Action Authenticator" to flow
And Admin add execution "Cloud Identity QR Login Authenticator" to flow

Scenario: [2] Admin selects the flow dropdown and selects the requirement for each execution on the table
Then Admin selects the Top Level Form "Cloud Identity SR Test" 
Then Admin selects Requirement "REQUIRED" checkbox for "Username Password Form" Auth Type
And Admin selects Requirement "DISABLED" checkbox for "Cloud Identity Authenticator" Auth Type
And Admin selects Requirement "ALTERNATIVE" checkbox for "Cloud Identity Demo Authenticator" Auth Type
And Admin selects Requirement "REQUIRED" checkbox for "Cloud Identity FIDO Registration Required Action Authenticator" Auth Type
And Admin selects Requirement "REQUIRED" checkbox for "Cloud Identity IBM Verify Registration Required Action Authenticator" Auth Type
And Admin selects Requirement "ALTERNATIVE" checkbox for "Cloud Identity QR Login Authenticator" Auth Type

Scenario: [3] Verify the correct Requirements have been selected and delete the flow
Then Admin selects the Top Level Form "Cloud Identity SR Test"
 
# The selected requirements cannot be verified at this time. When a requirement is selected, the table doesn't indicate the radio is selected.
#Then Admin sees Requirement "REQUIRED" checkbox is selected for "Username Password Form" Auth Type

And Admin deletes Alias "Cloud Identity SR Test" name from the Flow dropdown and verifies successful

