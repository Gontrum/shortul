package io.gontrum.shorturl.controller

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assert
import com.nhaarman.mockito_kotlin.*
import io.gontrum.shorturl.VIEW_ALL_STATISTICS
import io.gontrum.shorturl.VIEW_ALL_TOP_STATS
import io.gontrum.shorturl.VIEW_STATISTICS
import io.gontrum.shorturl.VIEW_TOP_STATS
import io.gontrum.shorturl.model.DayGroupResponse
import io.gontrum.shorturl.model.Statistic
import io.gontrum.shorturl.repository.StatisticsRepository
import io.gontrum.shorturl.service.AuthorizationCheckerService
import io.gontrum.shorturl.service.StatisticsService
import org.junit.Test
import org.springframework.security.core.Authentication
import java.util.*

class StatisticsControllerTest {

    private val NULL_USERNAME = null

    private var statisticsRepositoryMock = mock<StatisticsRepository>()
    private var statisticsServiceMock = mock<StatisticsService>()
    private var authenticationMock = mock<Authentication>()
    private var authorizationCheckerServiceMock = mock<AuthorizationCheckerService>()

    private var statisticsController: StatisticsController

    init {
        statisticsController = StatisticsController(statisticsRepositoryMock, statisticsServiceMock, authorizationCheckerServiceMock)
    }

    @Test
    fun statisticsForHashResultsIn404WhenNoResultWasFound() {
        arrangeForAdmin()
        val testHash = "testHash"
        whenever(statisticsRepositoryMock.findByHash(testHash)).thenReturn(null)

        val result = statisticsController.statisticsForHash(testHash, authenticationMock)

        verifyZeroInteractions(statisticsServiceMock)
        verify(statisticsRepositoryMock, never()).findByHashAndUsername(any(), any())
        assert.that(result.body, absent())
        assert.that(result.statusCodeValue, equalTo(404))
    }

    @Test
    fun statisticsForHashShouldCallfindByHashWhenCalledByAdmin() {
        arrangeForAdmin()
        val testHash = "testHash"
        whenever(statisticsRepositoryMock.findByHash(testHash)).thenReturn(null)

        statisticsController.statisticsForHash(testHash, authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findByHash(any())
        verify(statisticsRepositoryMock, never()).findByHashAndUsername(any(), any())
    }

    @Test
    fun statisticsForHashShouldCallFindByHashAndUsernameWhenCalledByUser() {
        arrangeForUser()
        val testHash = "testHash"
        whenever(statisticsRepositoryMock.findByHashAndUsername(any(), any())).thenReturn(null)

        statisticsController.statisticsForHash(testHash, authenticationMock)

        verify(statisticsRepositoryMock, never()).findByHash(any())
        verify(statisticsRepositoryMock, times(1)).findByHashAndUsername(any(), any())
    }

    @Test
    fun statisticsForHashReturnsResultWhenSomethingWasFound() {
        arrangeForAdmin()
        val testHash = "testHash"
        val assumedResult = Statistic(testHash, mapOf("Firefox" to 1), mapOf("Linux" to 1), listOf(Date()), 42, NULL_USERNAME)
        whenever(statisticsRepositoryMock.findByHash(testHash)).thenReturn(assumedResult)

        val result = statisticsController.statisticsForHash(testHash, authenticationMock).body ?: throw IllegalStateException()

        verify(statisticsRepositoryMock, times(1)).findByHash(eq(testHash))
        verifyZeroInteractions(statisticsServiceMock)
        assert.that(result, present())
        assert.that(result, equalTo(assumedResult))
    }

    @Test
    fun topFiveStatisticsReturnsEmptyObjectWhenNothingWasFound() {
        arrangeForAdmin()
        whenever(statisticsRepositoryMock.findTop5ByOrderByTimesInvocedDesc()).thenReturn(emptyList())

        val topFiveStatistics = statisticsController.topFiveStatistics(authenticationMock).body ?: throw IllegalStateException()

        verify(statisticsRepositoryMock, times(1)).findTop5ByOrderByTimesInvocedDesc()
        verifyZeroInteractions(statisticsServiceMock)
        assert.that(topFiveStatistics.toList(), isEmpty)
    }

    @Test
    fun topFiveStatisticsShouldCallFindTop5ByOrderByTimesInvocedDescWhenCalledByAdmin() {
        arrangeForAdmin()
        whenever(statisticsRepositoryMock.findTop5ByOrderByTimesInvocedDesc()).thenReturn(emptyList())

        statisticsController.topFiveStatistics(authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findTop5ByOrderByTimesInvocedDesc()
        verify(statisticsRepositoryMock, never()).findTop5ByAndUsernameOrderByTimesInvocedDesc(any())
    }

    @Test
    fun topFiveStatisticsShouldCallFindTop5ByAndUsernameOrderByTimesInvocedDescWhenCalledByUser() {
        arrangeForUser()
        whenever(statisticsRepositoryMock.findTop5ByAndUsernameOrderByTimesInvocedDesc(any())).thenReturn(emptyList())

        statisticsController.topFiveStatistics(authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findTop5ByAndUsernameOrderByTimesInvocedDesc(any())
        verify(statisticsRepositoryMock, never()).findTop5ByOrderByTimesInvocedDesc()
    }

    @Test
    fun topFiveStatisticsReturnsListOfObjectsWhenSomethingWasFound() {
        arrangeForAdmin()
        val assumedResults = listOf(
                Statistic("firstHash", mapOf("Firefox" to 1), mapOf("Linux" to 1), listOf(Date()), 42, NULL_USERNAME),
                Statistic("secondHash", mapOf("Safari" to 1), mapOf("OSX" to 1), listOf(Date()), 42, NULL_USERNAME)
        )
        whenever(statisticsRepositoryMock.findTop5ByOrderByTimesInvocedDesc()).thenReturn(assumedResults)

        val topFiveStatistics = statisticsController.topFiveStatistics(authenticationMock).body ?: throw IllegalStateException()

        verify(statisticsRepositoryMock, times(1)).findTop5ByOrderByTimesInvocedDesc()
        verifyZeroInteractions(statisticsServiceMock)

        assert.that(topFiveStatistics.toList(), hasElement(assumedResults[0]))
        assert.that(topFiveStatistics.toList(), hasElement(assumedResults[1]))
    }

    @Test
    fun getDateStatisticsForSingleHashResultsIn404WhenNothingWasFound() {
        arrangeForAdmin()
        val testHash = "testHash"
        whenever(statisticsRepositoryMock.findByHash(testHash)).thenReturn(null)

        val statisticResult = statisticsController.getDateStatisticsForSingleHash(testHash, authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findByHash(eq(testHash))
        verifyZeroInteractions(statisticsServiceMock)
        assert.that(statisticResult.body, absent())
        assert.that(statisticResult.statusCodeValue, equalTo(404))
    }

    @Test
    fun getDateStatisticsForSingleHashShouldCallFindByHashWhenCalledByAdmin() {
        arrangeForAdmin()
        val testHash = "testHash"
        whenever(statisticsRepositoryMock.findByHash(testHash)).thenReturn(null)

        statisticsController.getDateStatisticsForSingleHash(testHash, authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findByHash(any())
        verify(statisticsRepositoryMock, never()).findByHashAndUsername(any(), any())
    }

    @Test
    fun getDateStatisticsForSingleHashShouldCallFindByHashAndUserWhenCalledByUser() {
        arrangeForUser()
        val testHash = "testHash"
        whenever(statisticsRepositoryMock.findByHashAndUsername(any(), any())).thenReturn(null)

        statisticsController.getDateStatisticsForSingleHash(testHash, authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findByHashAndUsername(eq(testHash), eq("user"))
        verify(statisticsRepositoryMock, never()).findByHash(any())
    }

    @Test
    fun getDateStatisticsForSingleHashResultsInDateResultWhenSomethingWasFound() {
        arrangeForAdmin()
        val testHash = "testHash"
        val assumedResult = Statistic(testHash, mapOf("Firefox" to 1), mapOf("Linux" to 1), listOf(Date()), 42, NULL_USERNAME)
        whenever(statisticsRepositoryMock.findByHash(testHash)).thenReturn(assumedResult)
        whenever(statisticsServiceMock.countCallsGroupedByDay(assumedResult)).thenReturn(mapOf("1948_05_14" to 50))

        val dayGroupResponse = statisticsController.getDateStatisticsForSingleHash(testHash, authenticationMock).body ?: throw IllegalStateException()

        verify(statisticsRepositoryMock, times(1)).findByHash(eq(testHash))
        verify(statisticsServiceMock, times(1)).countCallsGroupedByDay(eq(assumedResult))
        assert.that(dayGroupResponse.hash, equalTo(testHash))
        assert.that(dayGroupResponse.countedCallsGroupedByDay, equalTo(mapOf("1948_05_14" to 50)))
    }

    @Test
    fun getDateStatisticsReturnEmptyObjectWhenNothingWasFound() {
        arrangeForAdmin()
        whenever(statisticsRepositoryMock.findAll()).thenReturn(emptyList())

        val dateStatistics = statisticsController.getDateStatistics(authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findAll()
        verifyZeroInteractions(statisticsServiceMock)
        assert.that(dateStatistics.toList(), isEmpty)
    }

    @Test
    fun getDateStatisticsResultsInMultipleStatisticsIfStatisticsWhereFound() {
        arrangeForAdmin()
        val firstResult = Statistic("firstHash", mapOf("Firefox" to 1), mapOf("Linux" to 1), listOf(Date()), 42, NULL_USERNAME)
        val secondResult = Statistic("secondHash", mapOf("Safari" to 1), mapOf("OSX" to 1), listOf(Date()), 42, NULL_USERNAME)
        val assumedResults = listOf(
                firstResult,
                secondResult
        )
        whenever(statisticsRepositoryMock.findAll()).thenReturn(assumedResults)
        whenever(statisticsServiceMock.countCallsGroupedByDay(firstResult)).thenReturn(mapOf("1948_05_14" to 50))
        whenever(statisticsServiceMock.countCallsGroupedByDay(secondResult)).thenReturn(mapOf("1945_05_08" to 50))

        val dateStatistics = statisticsController.getDateStatistics(authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findAll()
        verify(statisticsServiceMock, times(2)).countCallsGroupedByDay(any())

        assert.that( dateStatistics.toList(), anyElement(equalTo(DayGroupResponse("firstHash", mapOf("1948_05_14" to 50)))))
        assert.that( dateStatistics.toList(), anyElement(equalTo(DayGroupResponse("secondHash", mapOf("1945_05_08" to 50)))))
    }

    @Test
    fun getDateStatisticsShouldCallFindAllWhenCalledByAdmin(){
        arrangeForAdmin()
        whenever(statisticsRepositoryMock.findAll()).thenReturn(emptyList())

        statisticsController.getDateStatistics(authenticationMock)

        verify(statisticsRepositoryMock, never()).findByUsername(any())
        verify(statisticsRepositoryMock, times(1)).findAll()
    }

    @Test
    fun getDateStatisticsShouldCallFindByUsernameWhenCalledByAdmin(){
        arrangeForUser()
        whenever(statisticsRepositoryMock.findByUsername(any())).thenReturn(emptyList())

        statisticsController.getDateStatistics(authenticationMock)

        verify(statisticsRepositoryMock, times(1)).findByUsername("user")
        verify(statisticsRepositoryMock, never()).findAll()
    }

    private fun arrangeForAdmin() {
        whenever(authorizationCheckerServiceMock.getAuthoritiesAsStrings(any())).thenReturn(listOf(VIEW_ALL_STATISTICS, VIEW_ALL_TOP_STATS))
        whenever(authenticationMock.name).thenReturn("admin")
    }

    private fun arrangeForUser() {
        whenever(authorizationCheckerServiceMock.getAuthoritiesAsStrings(any())).thenReturn(listOf(VIEW_STATISTICS, VIEW_TOP_STATS))
        whenever(authenticationMock.name).thenReturn("user")
    }
}