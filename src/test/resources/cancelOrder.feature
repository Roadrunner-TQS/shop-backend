Feature: Cancel order

  Background:
    Given I am logged in
    And  I am in the home page
    And I have an order

  Scenario: Cancel my order
    Given I am in the order page
    Then I should see my orders
    When I click on the cancel button
#    Then I should see a confirmation message