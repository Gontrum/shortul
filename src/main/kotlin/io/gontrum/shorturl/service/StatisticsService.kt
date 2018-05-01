package io.gontrum.shorturl.service

import io.gontrum.shorturl.model.Statistic
import org.springframework.stereotype.Service
import java.time.ZoneId

@Service
class StatisticsService {
    fun countCallsGroupedByDay(allStatistics: Statistic): Map<String, Int> {
        return allStatistics.calledAt.groupingBy { calledAt ->
            val localDate = calledAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            "${localDate.year}_${localDate.monthValue}_${localDate.dayOfMonth}"
        }.eachCount()
    }
}