#Author kpresle
#Author nmtrimbl

Feature: Edit demographics
	As an iTrust Personnel
	I want to edit my demographics
	So that my information is stored for regulatory purposes

Scenario: Add my demographics
Given An admin exists in the system
When I log in as a personnel admin
When I navigate to the Personnel Edit My Demographics page
When I fill in new, updated personnel demographics
Then The personnel demographics are updated
And The admin can view new demographics

Scenario: Edit Demographics Responder
Given A sample ER exists in the system
When I log in as the sample ER
And I navigate to the Personnel Edit My Demographics page
And I fill in new, updated personnel demographics
Then The personnel demographics are updated
And The personnel can view new demographics