#Author: Deepak Mahapatra
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template

Feature: TalentHub Login Functionality
  As a registered TalentHub user
  I want to login with valid credentials
  So that I can access the application
  Background:
    Given I open the browser
    And I navigate to url
  @SmokeTest1
  Scenario: Successful login to TalentHub
    When User clicks on Login to App button
    And User enters username "dmahapatra@businessonetech.com"
    And User clicks on Next button
    And User enters password "Sambhuni@25"
    And User clicks on Sign In button
    And User clicks on Yes button
    Then User should be successfully logged in
  @SmokeTest
  Scenario: Successful Addition of New Candidate
    When User clicks on Login to App button
    And User enters username "dmahapatra@businessonetech.com"
    And User clicks on Next button
    And User enters password "Sambhuni@25"
    And User clicks on Sign In button
    And User clicks on Yes button
    Then User Clicks on Candidate Menu Button
    And User Clicks on Create Candidate Button
    And User Clicks on Select File button and uploads the profile in pdf format
    And User Selects the Value from Location dropdown
    And User Selects the Value from profile source dropdown
    And User enters jobId
    And User Clicks on Create Candidate Button Inside

##Feature: TalentHub Upload Resume one by one within the folder Functionality
  @SmokeTest2
  Scenario: Successful Addition of New Candidate
    When User clicks on Login to App button
    And User enters username "dmahapatra@businessonetech.com"
    And User clicks on Next button
    And User enters password "Sambhuni@25"
    And User clicks on Sign In button
    And User clicks on Yes button
    Then User Clicks on Candidate Menu Button
    And User Clicks on Create Candidate Button
    And User uploads profiles with jobname

