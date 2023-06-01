Feature: Add Item to Cart

  Background:
    Given I am logged in
    And  I am in the home page


  Scenario: Add Item to Cart
    When I click on the first item
    And  I click on the add to cart button
    Then I should see the item in the cart




