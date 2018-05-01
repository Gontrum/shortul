package io.gontrum.shorturl.model

data class ShortUrlResponse(
        val url: String,
        val shortUrl: String
){
    companion object {
        fun fromShortUrl(shortUrl: ShortUrl, domainName: String): ShortUrlResponse {
            return ShortUrlResponse(shortUrl.url, "${domainName}/${shortUrl.hash}")
        }
    }
}