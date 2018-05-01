package io.gontrum.shorturl.repository

import io.gontrum.shorturl.model.ShortUrl
import org.springframework.data.mongodb.repository.MongoRepository

interface ShortUrlRepository : MongoRepository<ShortUrl, String> {
    fun findByUrlAndUsernameNull(url: String): ShortUrl?
    fun findByUrlAndUsername(url: String, username: String): ShortUrl?

    fun findByHash(hash: String): ShortUrl?
}