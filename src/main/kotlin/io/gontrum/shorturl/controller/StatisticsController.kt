package io.gontrum.shorturl.controller

import io.gontrum.shorturl.VIEW_ALL_STATISTICS
import io.gontrum.shorturl.VIEW_ALL_TOP_STATS
import io.gontrum.shorturl.VIEW_STATISTICS
import io.gontrum.shorturl.VIEW_TOP_STATS
import io.gontrum.shorturl.exception.NotAuthorizedException
import io.gontrum.shorturl.model.DayGroupResponse
import io.gontrum.shorturl.model.Statistic
import io.gontrum.shorturl.repository.StatisticsRepository
import io.gontrum.shorturl.service.AuthorizationCheckerService
import io.gontrum.shorturl.service.StatisticsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class StatisticsController(private val statisticsRepository: StatisticsRepository,
                           private val statisticsService: StatisticsService,
                           private val authorizationCheckerService: AuthorizationCheckerService) {

    @GetMapping(path = ["/statistics/{hash}"], produces = ["application/json"])
    fun statisticsForHash(@PathVariable hash: String, authentication: Authentication): ResponseEntity<Statistic> {
        val authorities = authorizationCheckerService.getAuthoritiesAsStrings(authentication)
        authorizationCheckerService.checkAuthenticationFor(authorities, VIEW_ALL_STATISTICS, VIEW_STATISTICS)

        val statistic = if (authorities.contains(VIEW_ALL_STATISTICS)) {
            statisticsRepository.findByHash(hash) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            statisticsRepository.findByHashAndUsername(hash, authentication.name) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        return ResponseEntity(statistic, HttpStatus.OK)
    }

    @GetMapping(path = ["/statistics/top"], produces = ["application/json"])
    fun topFiveStatistics(authentication: Authentication): ResponseEntity<Iterable<Statistic>> {
        val authorities = authorizationCheckerService.getAuthoritiesAsStrings(authentication)
        authorizationCheckerService.checkAuthenticationFor(authorities, VIEW_ALL_TOP_STATS, VIEW_TOP_STATS)

        val topFiveStatistics = if (authorities.contains(VIEW_ALL_TOP_STATS)) {
            statisticsRepository.findTop5ByOrderByTimesInvocedDesc()
        } else {
            statisticsRepository.findTop5ByAndUsernameOrderByTimesInvocedDesc(authentication.name)
        }
        return ResponseEntity(topFiveStatistics, HttpStatus.OK)
    }

    @GetMapping(path = ["/statistics/{hash}/date"], produces = ["application/json"])
    fun getDateStatisticsForSingleHash(@PathVariable hash: String, authentication: Authentication): ResponseEntity<DayGroupResponse> {
        val authorities = authorizationCheckerService.getAuthoritiesAsStrings(authentication)
        authorizationCheckerService.checkAuthenticationFor(authorities, VIEW_ALL_STATISTICS, VIEW_STATISTICS)

        val allStatistics = if (authorities.contains(VIEW_ALL_STATISTICS)) {
            statisticsRepository.findByHash(hash) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        } else {
            statisticsRepository.findByHashAndUsername(hash, authentication.name) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        }
        val countedCallsGroupedByDay = statisticsService.countCallsGroupedByDay(allStatistics)
        return ResponseEntity(DayGroupResponse(hash, countedCallsGroupedByDay), HttpStatus.OK)
    }

    @GetMapping(path = ["/statistics/date"], produces = ["application/json"])
    fun getDateStatistics(authentication: Authentication): Iterable<DayGroupResponse> {
        val authorities = authorizationCheckerService.getAuthoritiesAsStrings(authentication)
        authorizationCheckerService.checkAuthenticationFor(authorities, VIEW_ALL_STATISTICS, VIEW_STATISTICS)

        val allStatistics = if (authorities.contains(VIEW_ALL_STATISTICS)) {
            statisticsRepository.findAll()
        } else {
            statisticsRepository.findByUsername(authentication.name)
        }

        return allStatistics.map { statistic ->
            val countedCallsGroupedByDay = statisticsService.countCallsGroupedByDay(statistic)
            DayGroupResponse(statistic.hash, countedCallsGroupedByDay)
        }
    }
}

