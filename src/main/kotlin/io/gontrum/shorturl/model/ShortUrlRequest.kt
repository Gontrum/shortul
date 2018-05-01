package io.gontrum.shorturl.model

import org.hibernate.validator.constraints.URL

data class ShortUrlRequest(@get:URL val url: String)