package io.gontrum.shorturl.model

data class DayGroupResponse(
        val hash: String,
        val countedCallsGroupedByDay: Map<String, Int>
)