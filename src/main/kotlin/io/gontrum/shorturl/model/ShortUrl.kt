package io.gontrum.shorturl.model

import org.hibernate.validator.constraints.URL
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.Max

@Document
data class ShortUrl(
        @get:URL val url: String,
        @get:Max(5) val hash: String,
        val username: String?
)