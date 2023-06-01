Feature: Remove Item from Cart

  Background:
    Given I am logged in
    And  I am in the home page
    And  I have items in my cart

  Scenario: Add Item to Cart
    When I should see the item in the cart
    And  I click on the remove button
    Then I should not see the item in the cart
