Feature: called Urls create callable statistics
  Scenario: client can receive statistics for called Urls
    Given the url https://gontrum.io with the hash 4712
    When the client with the useragent Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.117 Safari/537.36 gets the url /4712
    Then the client receives a redirect to https://gontrum.io
    When the admin gets the url /statistics/4712
    Then the admin receives statistics with OS Mac OS X browser Chrome and invoces 1

  Scenario: client can receive statistics for called Urls
    Given the url https://gontrum.io with the hash 4713
    When the client with the useragent Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.117 Safari/537.36 gets the url /4713
    When the client with the useragent Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:58.0) Gecko/20100101 Firefox/58.0 gets the url /4713
    When the admin gets the url /statistics/4713
    Then the admin receives statistics with OS Mac OS X browser Firefox 58 and invoces 2

  Scenario: client can receive the top five statistics
    Given the url https://gontrum.io with the hash top1
    Given the url https://gontrum.io with the hash top2
    Given the url https://gontrum.io with the hash top3
    Given the url https://gontrum.io with the hash top4
    Given the url https://gontrum.io with the hash top5
    Given the url https://gontrum.io with the hash notlisted
    When for 7 times the client gets the url /top1
    When for 6 times the client gets the url /top2
    When for 5 times the client gets the url /top3
    When for 4 times the client gets the url /top4
    When for 3 times the client gets the url /top5
    When the client gets the url /notlistet
    When the admin gets the url /statistics/top
    Then the admin receives statistics containing hash top1 on place 0
    Then the admin receives statistics containing hash top2 on place 1
    Then the admin receives statistics containing hash top3 on place 2
    Then the admin receives statistics containing hash top4 on place 3
    Then the admin receives statistics containing hash top5 on place 4
    Then the admin receives statistics containing 5 items