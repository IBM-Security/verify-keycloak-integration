@Victoria
Feature: Users page elements verification

Scenario: [1] Verify elemends on Users page
Given Customer logs into the "Admin Console" with username "Admin"
Then Customer clicks on Users Link
Then Customer verifies Users page elements
Then Customer clicks Add new user button
Then Customer verifies elements on AddUser page