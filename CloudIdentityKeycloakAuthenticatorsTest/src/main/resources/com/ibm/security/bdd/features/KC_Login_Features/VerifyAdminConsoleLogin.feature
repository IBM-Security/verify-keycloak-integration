@Publish @JT
Feature: Verify Administrator Console Login Successful
This feature navigates from Keycloak homepage to Admin Console Login page, verifies the elements on the Login page, 
and logs in as the Admin user.

Scenario: [1] Navigate to the Admin Console login page and verifies the elements on the page
Given Customer navigates to the Admin Console Login page
Then Customer verifies the Keycloak icon is present
And Customer verifies the Log In title is present
And Customer verifies the Username field is present
And Customer verifies the Password field is present
And Customer verifies the Login button is present

Scenario: [2] Verify the login is unsuccessful when a wrong username or password is used. 
Given Customer navigates to the Admin Console Login page
Then Customer verifies an error is displayed using an incorrect username
And Customer verifies an error is displayed using an incorrect password
And Customer verifies an error is displayed using an empty username or password

Scenario: [3] Verify the login is successful with the correct username and password.
Given Customer navigates to the Admin Console Login page
Then Customer logs in with username "admin" and password "admin"
And Customer verifies Admin Console login is successful

    

  