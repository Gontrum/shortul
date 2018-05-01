package io.gontrum.shorturl.service

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import io.gontrum.shorturl.model.Statistic
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale


class StatisticsServiceTest {

    @Test
    fun countCallsGroupedByDayShouldGroupInTheRightFormat() {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN)

        val statistic = Statistic(
                "hash",
                mapOf("Firefox" to 1),
                mapOf("OSX" to 1),
                listOf(
                        df.parse("1945-05-08"),
                        df.parse("1945-05-08"),
                        df.parse("1945-05-08"),
                        df.parse("1948-05-14"),
                        df.parse("1948-05-14")
                ),
                5,
                null
        )
        val groupedStats = StatisticsService().countCallsGroupedByDay(statistic)

        val assumedMap = mapOf("1945_5_8" to 3, "1948_5_14" to 2)

        assert.that(groupedStats.size, equalTo(2))
        assert.that(groupedStats, equalTo(assumedMap))
    }
}