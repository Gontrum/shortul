package io.gontrum.shorturl.controller

import io.gontrum.shorturl.model.ShortUrlRequest
import io.gontrum.shorturl.model.ShortUrlResponse
import io.gontrum.shorturl.repository.ShortUrlRepository
import io.gontrum.shorturl.service.ShortUrlCreator
import io.gontrum.shorturl.service.DataCollector
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid


@RestController
class ShortenController(private val shortUrlRepository: ShortUrlRepository,
                        private val shortUrlCreator: ShortUrlCreator,
                        private val dataCollector: DataCollector) {

    @Value("\${domain.name}")
    private val domainName = "http://fallback.io"

    @PostMapping(path = ["/shorten"], produces = ["application/json"])
    fun shorten(@Valid @RequestBody shortUrlRequest: ShortUrlRequest, principal: Principal?): ResponseEntity<ShortUrlResponse> {
        val username = principal?.name

        val existingShortUrl = if (username == null) {
            shortUrlRepository.findByUrlAndUsernameNull(shortUrlRequest.url)
        } else {
            shortUrlRepository.findByUrlAndUsername(shortUrlRequest.url, username)
        }

        if (existingShortUrl != null) {
            return ResponseEntity(ShortUrlResponse.fromShortUrl(existingShortUrl, domainName), HttpStatus.OK)
        } else {
            val shortUrl = shortUrlRepository.save(shortUrlCreator.create(shortUrlRequest.url, username))
            return ResponseEntity(ShortUrlResponse.fromShortUrl(shortUrl, domainName), HttpStatus.CREATED)
        }
    }

    @GetMapping(path = ["/{hash}"])
    fun redirectFromHash(
            @PathVariable hash: String,
            response: HttpServletResponse,
            @RequestHeader(required = false, value = "User-Agent") userAgent: String?
    ) {
        val shortUrl = shortUrlRepository.findByHash(hash)
        if (shortUrl != null) {
            dataCollector.collectVisit(shortUrl, userAgent ?: "", OffsetDateTime.now())
            response.sendRedirect(shortUrl.url)
        } else {
            response.sendError(404)
        }
    }
}

