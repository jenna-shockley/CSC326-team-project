#Author jnshockl

Feature: Personal Representatives
	As a patient
	I want to be able to add and remove PRs for myself and remove myself as a PR for other patients
	So that I can make important health decisions for my family members and they can make them for me
	As an HCP
	I want to be able to declare PRs for my patients
	So that I can ensure that they are in the capable hands of their loved ones


Scenario: Add PR
	Given the user is a patient
	When I add a patient as their PR
	Then the specified patient is added to the user's list of PRs

Scenario: Remove PR
	Given the user is a patient
	When the user goes to remove a PR and selects one of their current PRs
	Then that patient is removed from the user's list of PRs

Scenario: Remove self as PR
	Given the user is a PR
	When the user goes to remove themself as a PR and selects someone they represent
	Then that person is removed from the user's list of patients they represent

Scenario: HCP declare PR
	Given the user is an HCP
	When the HCP selects a patient, goes to add a PR for that patient, and enters the name of another patient
	Then the second patient is added to the list of PRs for the first patient 