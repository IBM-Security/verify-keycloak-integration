@Publish
Feature: Create and delete top level authentication form 
This feature verifies the creation and the deletion of top level form in the Authentication module

  
Scenario: [1] Login, navigate to Authentication, click New button to Create Top Level Form 
Given "admin" logs into the "Admin Console" and navigate to "Authentication"
Then Admin creates new Top Level Form with the following parameters
| Alias         | Description              | Flow Type |
| New Flow Test | Verify New Flow Creation | generic  |

Scenario: [2] Verify the Top Level Form has been created and can be deleted successfully
Given "admin" logs into the "Admin Console" and navigate to "Authentication"
Then Admin sees new Alias "New Flow Test" name created on the Flow dropdown
Then Admin selects the Top Level Form "New Flow Test" 
#Then Admin verifies the Description "New Flow Creation" tooltip
Then Admin deletes Alias "New Flow Test" name from the Flow dropdown and verifies successful


    
