package io.gontrum.shorturl.statistics

import eu.bitwalker.useragentutils.UserAgent
import io.gontrum.shorturl.model.ShortUrl
import io.gontrum.shorturl.repository.StatisticsRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

@Service
class DataCollector(private val statisticsRepository: StatisticsRepository) {
    fun collectVisit(shortUrl: ShortUrl, userAgent: String, calledAt: OffsetDateTime) {
        val parsedUserAgent = UserAgent.parseUserAgentString(userAgent)
        val browser = parsedUserAgent.browser.getName()
        val os = parsedUserAgent.operatingSystem.getName()
        statisticsRepository.saveCall(shortUrl.hash, shortUrl.username, browser, os, Date.from(calledAt.toInstant()))
    }
}