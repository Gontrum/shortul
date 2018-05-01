package io.gontrum.shorturl.exception

class StatisticsNotFoundException(hash: String) : Throwable("Statistic with hash $hash could not be found")