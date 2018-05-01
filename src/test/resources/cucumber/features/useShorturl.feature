Feature: a created shortUrl can be used to redirect
  Scenario: client makes call to a given hash
    Given the url https://gontrum.io with the hash 4711
    When the client gets the url /4711
    Then the client receives a redirect to https://gontrum.io

  Scenario: client makes a call to a not given hash
    Given the url https://gontrum.io with the hash 0815
    When the client gets the url /notdefinedhash
    Then the client receives status code of 404
    And the client receives not found
