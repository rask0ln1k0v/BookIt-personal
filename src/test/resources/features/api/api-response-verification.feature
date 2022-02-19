Feature: Get available rooms

  @ui @rooms @db
Scenario: Available rooms verification UI and API
  Given User logged in Bookit api as team lead role
  When User sends GET request to "/api/rooms/available" with following informations:
    | year            | 2022       |
    | month           | 2          |
    | day             | 21         |
    | conference-type | SOLID      |
    | cluster-name    | light-side |
    | timeline-id     | 11237      |
    Given User logged in Bookit app as team lead role
    When User goes to room hunt page
    And User searches for room with date:
      | date | February 21, 2022 |
      | from | 7:00am            |
      | to   | 7:30am            |
  Then Json response names must match the UI result room names.
    And available rooms in database should match UI and API results
