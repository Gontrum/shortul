package io.gontrum.shorturl.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "statistics")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Statistic(
        val hash: String,
        val browser: Map<String, Int>,
        val os: Map<String, Int>,
        val calledAt: List<Date>,
        val timesInvoced: Int,
        val username: String?
)