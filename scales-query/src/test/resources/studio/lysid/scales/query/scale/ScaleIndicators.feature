Feature: The Scale aggregate Indicators
  
  Test the business rules and invariants manipulating Indicators within a Scale.
  
  
  Scenario: An Indicator can be attached to a Draft Scale
    Given a Draft Scale
    When the Indicator "someIndicator" is attached to the Scale
    Then the Scale should contain exactly the following Indicators: someIndicator
  
  Scenario: An Indicator can be used only once in a Scale
    Given a Draft Scale with the following Indicators attached: A, B, C
    When the Indicator "A" is attached to the Scale
    Then it should fail with message "The indicator 'A' is already attached to the Scale. An Indicator can be used only once in a Scale."
  
  
  Scenario: Several Indicators can be attached to a Draft Scale
    Given a Draft Scale
    When the following Indicators are attached to the Scale: A, B, C
    Then the Scale should contain exactly the following Indicators: A, B, C
    
    
  Scenario: Indicators can be reordered
    Given a Draft Scale with the following Indicators attached: A, B, C, D, E
    When the Indicators are reordered like this: A, C, E, B, D
    Then the Scale should contain exactly the following Indicators: A, C, E, B, D
    
    