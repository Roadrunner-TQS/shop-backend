Feature: Checkout page
  description: |
    The checkout page is the page where the user can see the items in their cart and proceed to checkout.
    The user chosse the pickup location and proceed to checkout.


  Background:
    Given I am logged in
    And I have items in my cart
    And I am on the cart page

Scenario: Checkout page
    When I click on the checkout button
    Then I should see the checkout page
    And I am filling the checkout form
    And I select the pickup option
    When I click on the submit button
    Then I should see the confirmation the pagament was successful