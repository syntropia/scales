Feature: The Scale aggregate
  
  Test the business rules and invariants for the Scale aggregate.
  
  
  Scenario: A Scale is created in Draft state
    When I create a new Scale
    Then its status should be Draft
  
    
  Scenario: Publishing a Scale changes its state into Published
    Given a Draft Scale
    When I publish this Scale
    Then its status should be Published
  
    
  Scenario Outline: A Scale can only be published when in Draft state
    Given a <initialState> Scale
    When I publish this Scale
    Then it should fail with message "A Scale can be published only when it has a Draft status."
    
    Examples:
      | initialState |
      | Published    |
      | Archived     |
      | Evolved      |
  
    
  Scenario: An Archived Scale cannot be archived again
    Given an Archived Scale
    When I archive this Scale
    Then it should fail with message "A Scale can be archived only when it has a Draft, Published or Evolved status."
  
    
  Scenario Outline: Unarchiving a Scale changes its state back into its original state
    Given an Archived Scale previously in <previousState> state
    When I unarchive this Scale
    Then its status should be <previousState>
    
    Examples:
      | previousState |
      | Draft         |
      | Published     |
      | Evolved       |
  
  
  Scenario Outline: Only Archived Scale can be unarchived
    Given an <initialState> Scale
    When I unarchive this Scale
    Then it should fail with message "A Scale can be unarchived only when it has an Archived status."
    
    Examples:
      | initialState |
      | Draft        |
      | Published    |
      | Evolved      |
  
  
  Scenario: Evolving a Scale into another one
    Given a Published Scale
    And another Draft Scale
    When I evolve one into the other
    Then the former Scale status should be Evolved
    And it should designate the latter as its evolved version
    
    
  Scenario Outline: A Scale can be edited only in Draft state
    Given a <initialState> Scale
    When I attach an Indicator to the Scale
    Then it should fail with message "A Scale can be edited only when it has a Draft status."
    
    Examples:
      | initialState |
      | Published    |
      | Archived     |
      | Evolved      |
    
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
    