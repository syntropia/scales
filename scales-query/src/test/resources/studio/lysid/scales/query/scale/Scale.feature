Feature: The Scale aggregate
  
  Test the business rules and invariants for the Scale aggregate.
  
  
  Scenario: A Scale is created in Draft state
    When I create a new Scale
    Then its status should be Draft
  
  Scenario: Publishing a Scale changes its state into Published
    Given a Draft Scale
    When I publish this Scale
    Then its status should be Published
  
  Scenario Outline: An Indicator can only be published when in Draft state
    Given a <initialState> Scale
    When I publish this Scale
    Then it should fail with message "A Scale can be published only when it has a Draft status."
    
    Examples:
      | initialState |
      | Published    |
      | Archived     |
      | Evolved      |
  
  
