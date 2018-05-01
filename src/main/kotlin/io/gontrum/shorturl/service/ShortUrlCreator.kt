package io.gontrum.shorturl.service

import com.google.common.hash.Hashing
import io.gontrum.shorturl.model.ShortUrl
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class ShortUrlCreator {
    fun create(url: String, username: String? = ""): ShortUrl {
        return ShortUrl(
                url = url,
                hash = Hashing.murmur3_32().hashString("${url}${username}", StandardCharsets.UTF_8).toString(),
                username = username
        )
    }
}
