Feature: Manage Lab Procedures
	As an HCP
	I want to record lab procedures for patients
	So that I have record of past and future lab procedures
	As a patient
	I want to view my past and upcoming lab procedures
	So that I can make sure they match my expectations
	As a labtech
	I want to view and edit lab procedures assigned to me
	So that I know the work I have to do
	
@addproc
Scenario Outline: Add Lab Procedure to an Office Visit
    Given I have logged in with this username: <user>
    When I start documenting an office visit for patient with name: <first> <last> and date of birth: <dob>
    And fill in office visit with date: <date>, hospital: <hospital>, notes: <notes>, weight: <weight>, height: <height>, blood pressure: <pressure>, household smoking status: <hss>, patient smoking status: <pss>, hdl: <hdl>, ldl: <ldl>, and triglycerides: <triglycerides>
    And add a lab procedure with a lab code of <labcode>, lab tech of <labtech>, priority of <priority> with <comments> comments
    And submit office visit 
   	Then A message indicates visit was submitted successfully

Examples:
	| user  | first | last | dob        | date       | hospital         | notes                                                              | weight | height | pressure | hss     | pss   | hdl | ldl | triglycerides | labcode | labtech | priority | comments      |
	| svang | Jim   | Bean | 04/12/1995 | 12/15/2017 | General Hospital | Jim appears to be depressed. He also needs to eat more vegetables. | 130.4  | 73.1   | 160/80   | OUTDOOR | NEVER | 50  | 123 | 148           | 22222-6 | labtech | High     | Make it quick |


@viewproc
Scenario Outline: View Lab Procedures
	Given I have logged in with this username: <user>
	When I choose to view my office visit
	Then I see a lab procedure with a lab code of <labcode>, date of <date>, priority of <priority> with <comments> comments

Examples:
	| user  | labcode | date       | priority | comments      |
	| jbean | 22222-6 | 12/15/2017 | High     | Make it quick |
	
	
@deleteproc
Scenario Outline: Remove Lab Procedure
	Given I have logged in with this username: <user>
	When I choose to edit an office visit with a lab procedure attached to it
	Then I remove a lab procedure with a lab code of <labcode>, date of <date>, priority of <priority> with <comments> comments
	And submit office visit 
   	Then A message indicates visit was edited successfully

Examples:
	| user  | labcode | date       | priority | comments      |
	| svang | 22222-6 | 12/15/2017 | High     | Make it quick |
