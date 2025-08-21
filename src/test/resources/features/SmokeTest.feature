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

Feature: Access Playground section on WishInfinite website

  Background:
    Given I open the browser
    And I navigate to url
@SmokeTest
  Scenario: Verify Playground section is visible
    Then I should see the Playground section visible
@Test
  Scenario: Verify page title contains WishInfinite
    Then The page title should contain "WishInfinite"
@SmokeTest    
  Scenario: Verify user enters text to the input Box
    Then User clicks on playground button
  	Then User enters "Deepak" to text box 
