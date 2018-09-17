#Author kpresle
#Author nmtrimbl

Feature: Add a User
	As an Admin
	I want to add a new user
	So that someone new can use iTrust

Scenario: Add new user
Given The user does not already exist in the database
When I log in as admin
When I navigate to the Add User page
When I fill in the values in the Add User form
Then The user is created successfully
And The new user can login

Scenario: Add Labtech as Admin
Given The labtech does not already exist in the database
When I log in as admin
And I navigate to the Add User page
And I fill in the values with the role as labtech in the Add User form
Then The user is created successfully
And The new labtech can login