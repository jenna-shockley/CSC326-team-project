Feature: Lab Codes
	As an admin
	I want to be able to create, edit, and delete lab codes
	So that the doctors in my hospital have access to lab procedures for their patients

@addcode
Scenario Outline: Create lab code
	Given I log in with username: <user>
	When I create a new lab code with code: <code>, longName: <longName>, component: <component>, property: <property>, and method: <method>
	Then a new lab code will be created
	
Examples:
	| user  | code    | longName | component | property | method |
	| admin | 00000-0 | lc1      | comp      | prop     | meth   |

@editcode
Scenario Outline: Edit lab code
	Given I log in with username: <user>
	When I edit a lab code and re-fill the form with code: <code>, longName: <longName>, component: <component>, property: <property>, and method: <method>
	Then the lab code is updated
	
Examples:
	| user  | code    | longName | component | property | method |
	| admin | 11111-1 | lc2      | comp2     | prop2    | meth2  |

@deletecode
Scenario Outline: Delete lab code
	Given I log in with username: <user>
	When I delete a lab code with code: <code>, longName: <longName>, component: <component>, property: <property>, and method: <method>
	Then the lab code is deleted from the database

Examples:
	| user  | code    | longName | component | property | method |
	| admin | 11111-1 | lc2      | comp2     | prop2    | meth2  |
