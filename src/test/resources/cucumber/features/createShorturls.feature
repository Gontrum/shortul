Feature: a url can be shortened
  Scenario: client makes call to POST /shorten
    When the client posts the url https://gontrum.io to /shorten
    Then the client receives status code of 201
    And the client receives a shorturl
    And the client receives the url https://gontrum.io

  Scenario: client makes the same POST call to /shorten two times
    When the client posts the url https://double.io to /shorten
    When the client posts the url https://double.io to /shorten
    Then the client receives status code of 200
    And the client receives a shorturl
    And the client receives the url https://double.io

  Scenario: client makes a POST with invalid URL to /shorten
    When the client posts the url invalidXYZ to /shorten
    Then the client receives status code of 422
    And the client receives the error invalid url
