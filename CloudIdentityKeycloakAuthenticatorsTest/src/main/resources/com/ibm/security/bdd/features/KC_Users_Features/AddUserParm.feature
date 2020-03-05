@vPublish
Feature: This feature will Add user

Scenario: [0] If user vberengu exists - delete it
Given Customer logs into the "Admin Console" with username "Admin"
Given Customer clicks on Users Link
Then Customer deletes user if exists
	
Scenario: [1] Add user using parameters
Given Customer logs into the "Admin Console" with username "Admin"
Given Customer clicks on Users Link
Given Customer clicks Add new user button
Then Customer enters Username like "vberengu"
Then Customer enters email like "vberengu@us.ibm.com"
Then Customer enters First Name like "Victoria"
Then Customer enters Last Name like "Berengut"
Then Customer verifies User Enabled switch is ON
Then Customer verifies Email Verified switch is OFF
Then Customer clicks Save button to save new user
Then Customer sets the password for new user


Scenario: [2] Verify user created successfully
Given Customer logs into the "Admin Console" with username "Admin"
Given Customer clicks on Users Link
Then Customer verifies that user added successfully

Scenario: [3] Delete user
Given Customer logs into the "Admin Console" with username "Admin"
Given Customer clicks on Users Link
Then Customer deletes user
Then Customer verifies user deleted successfully

