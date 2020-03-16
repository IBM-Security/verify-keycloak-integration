@Publish
Feature: Create and delete user form 

Scenario: [0] Delete "kcuser" if exists
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then delete "kcuser" if exists

Scenario: [1] Login, navigate to Users, click Add user button to Create a new user
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then Admin creates new user form with the following parameters
| Username	| Email				 | First Name	| Last Name |
| kcuser	  | kcuser@keycloak.com | Keycloak 	| Token	|

Scenario: [2] Verify elements on Keycloak Account Managment pages
Given Customer logs into the "User Console" with username "kcuser"
Then Verify elements on Account panel
Then Verify elements on Password panel
Then Verify elements on Authenticator panel
Then Verify elements on Sessions
Then Verify elements on Application

Scenario: [3] search and delete user "kcuser"
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then search for "kcuser" and delete

Scenario: [4] verify user deleted successfully
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then Verify user "kcuser" deleted successfully