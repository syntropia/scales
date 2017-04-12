Feature: Indicators may be grouped as a set of related items in the context of the Scale.
  
  Test handling of hierarchical indicator groups
  
  Scenario: An Indicator group can be attached to an empty Scale
    Given a Draft Scale
    And the Indicator group "Group A" containing the indicators: A1, A2, A3
    When it is attached to the Scale
    Then the Scale should contain exactly the following Indicators: Group A