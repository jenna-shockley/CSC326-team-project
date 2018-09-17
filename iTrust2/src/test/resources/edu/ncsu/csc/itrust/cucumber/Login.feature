#Author nmtrimbl

Feature: Log In
  As an iTrust2 user
  I want to login
  So that I can use the system
  
Scenario: Login Labtech
Given A sample labtech exists in the system
When I login as the sample labtech
Then A list of log entries appears
And There is an edit drop down menu
And There is a View Source tab
And There is an About tab
And There is a Change Password option
And There is a Log Out button
And My username, labtech, is displayed