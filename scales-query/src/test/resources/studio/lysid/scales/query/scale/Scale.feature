Feature: The Scale aggregate
  
  Test the business rules and invariants for the Scale aggregate.
  
  
  Scenario: A Scale is created in Draft state
    When I create a new Scale
    Then its status should be Draft
  
  Scenario: Publishing a Scale changes its state into Published
    Given a Draft Scale
    When I publish this Scale
    Then its status should be Published
  
