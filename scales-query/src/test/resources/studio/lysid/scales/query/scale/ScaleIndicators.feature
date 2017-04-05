Feature: The Scale aggregate Indicators
  
  Test the business rules and invariants manipulating Indicators within a Scale.
  
  
  Scenario: An Indicator can be attached to a Draft Scale
    Given a Draft Scale
    And an Indicator named "FirstIndicator"
    When the Indicator "FirstIndicator" is attached to the Scale
    Then the Scale should contain "FirstIndicator"
    
    
  Scenario: Several Indicators can be attached to a Draft Scale
    Given a Draft Scale
    And the following Indicators:
      | indicatorName   |
      | FirstIndicator  |
      | SecondIndicator |
      | ThirdIndicator  |
    When these Indicators are attached to the Scale
    Then the Scale should contain exactly the following Indicators:
      | indicatorName   |
      | FirstIndicator  |
      | SecondIndicator |
      | ThirdIndicator  |
    