Feature: Track my order

  Background:
    Given I am logged in
    And  I am in the home page
    And I have an order


  Scenario: Track my order
    Given I am in the order page
    Then I should see my orders
    And I should see the order status

