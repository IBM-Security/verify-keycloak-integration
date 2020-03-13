@Publish
Feature: Create and delete users form 

Scenario: [0] Delete "vberengu" if exists
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then delete "vberengu" if exists

Scenario: [1] Login, navigate to Users, click Add user button to Create a new user
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then Admin creates new user form with the following parameters
| Username	| Email								| First Name	| Last Name |
| vberengu	| vberengu@us.ibm.com | Victoria 		| Berengut	|

Scenario: [2] search and delete user "vberengu"
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then search for "vberengu" and delete

Scenario: [3] verify user deleted successfully
Given "admin" logs into the "Admin Console" and navigate to "Users"
Then Verify user "vberengu" deleted successfully