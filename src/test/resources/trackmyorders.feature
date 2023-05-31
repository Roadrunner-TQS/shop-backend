Feature: Track my order

  Background:
    Given I am logged in
    And  I am in the home page


  Scenario: Track my order
    Given I am in the home page
    When I click on orders
    Then I should see my orders
    And I should see the order status

