Feature: The Scale aggregate Indicators
  
  Test the business rules and invariants manipulating Indicators within a Scale.
  
  
  # Attach indicators
  
  Scenario: An Indicator can be attached to a Draft Scale
    Given a Draft Scale
    When the Indicator "someIndicator" is attached to the Scale
    Then the Scale should contain exactly the following Indicators: someIndicator
  
    
  Scenario: An Indicator can be used only once in a Scale
    Given a Draft Scale with the following Indicators attached: A, B, C
    When the Indicator "A" is attached to the Scale
    Then it should fail with message "The Indicator 'A' is already attached to the Scale. An Indicator can be used only once in a Scale."
  
  
  Scenario: Several Indicators can be attached to a Draft Scale
    Given a Draft Scale
    When the following Indicators are attached to the Scale: A, B, C
    Then the Scale should contain exactly the following Indicators: A, B, C
    
  
    
  # Reorder indicators
  
  Scenario: Indicators can be reordered
    Given a Draft Scale with the following Indicators attached: A, B, C, D, E
    When the Indicators are reordered like this: A, C, E, B, D
    Then the Scale should contain exactly the following Indicators: A, C, E, B, D
  
    
  Scenario: Reordered Indicators must match already attached Indicator
    Given a Draft Scale with the following Indicators attached: A, B, C
    When the Indicators are reordered like this: A, C, E, B, D
    Then it should fail with message "The Indicator 'E' was not previously attached to this scale"
    
    
  Scenario: Reordering Indicators cannot apply to a Scale without any Indicator
    Given a Draft Scale
    When the Indicators are reordered like this: A, C, E, B, D
    Then it should fail with message "No Indicator has been added to this scale yet"
    
  
    
  # Detach indicators
  
  Scenario: An Indicator can be detached from a Draft Scale
    Given a Draft Scale with the following Indicators attached: A, B, C
    When the Indicator "B" is detached from the Scale
    Then the Scale should contain exactly the following Indicators: A, C

    
  Scenario: An Indicator cannot be detached if it has not been previously attached
    Given a Draft Scale with the following Indicators attached: A, B, C
    When the Indicator "Z" is detached from the Scale
    Then it should fail with message "The Indicator 'Z' was not previously attached to this scale"

    
  Scenario: Detaching an Indicator cannot apply to a Scale without any Indicator
    Given a Draft Scale
    When the Indicator "Z" is detached from the Scale
    Then it should fail with message "No Indicator has been added to this scale yet"
