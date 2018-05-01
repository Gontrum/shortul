package io.gontrum.shorturl.controller

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.endsWith
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import com.natpryce.hamkrest.startsWith
import com.nhaarman.mockito_kotlin.*
import io.gontrum.shorturl.model.ShortUrl
import io.gontrum.shorturl.model.ShortUrlRequest
import io.gontrum.shorturl.repository.ShortUrlRepository
import io.gontrum.shorturl.service.ShortUrlCreator
import io.gontrum.shorturl.statistics.DataCollector
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import javax.servlet.http.HttpServletResponse


class ShortenControllerTest {

    private val NULL_PRINCIPAL = null
    private val NULL_USER = null

    private val shortUrlRepository = mock<ShortUrlRepository>()
    private val shortUrlCreator = mock<ShortUrlCreator>()
    private val dataCollector = mock<DataCollector>()

    private var shortenController: ShortenController


    init {
        shortenController = ShortenController(shortUrlRepository, shortUrlCreator, dataCollector)
    }

    @Test
    fun shortenNewUrlShouldSaveUrlAndCreateHash() {
        val urlToBeShortened = "http://url-to-be-shortened.io"
        val hashToBeAssumed = "testHash"

        repositoryShouldNotFindUrl()
        configureRepositoryToReturnWhatWasSaved()
        creatorShouldReturnHashForUrl(hashToBeAssumed, urlToBeShortened)

        val shortUrlResponse = shortenController.shorten(ShortUrlRequest(urlToBeShortened), NULL_PRINCIPAL).body
                ?: throw IllegalStateException()

        verify(shortUrlCreator, times(1)).create(urlToBeShortened, NULL_USER)
        verify(shortUrlRepository, times(1)).findByUrlAndUsernameNull(urlToBeShortened)
        verify(shortUrlRepository, times(1)).save(ShortUrl(urlToBeShortened, hashToBeAssumed, NULL_USER))
        verify(dataCollector, never()).collectVisit(any(), any(), any())

        assert.that(shortUrlResponse, present())
        assert.that(shortUrlResponse.url, equalTo(urlToBeShortened))
        assert.that(shortUrlResponse.shortUrl, endsWith(hashToBeAssumed))
    }

    @Test
    fun shortenAlreadySavedUrlShouldNotCallSaveAndReturnAssociatedHash() {
        val urlToBeShortened = "http://url-to-be-shortened.io"
        val hashToBeAssumed = "testHash"

        repositoryShouldFindUrlWithHash(urlToBeShortened, hashToBeAssumed)

        val shortUrlResponse = shortenController.shorten(ShortUrlRequest(urlToBeShortened), NULL_PRINCIPAL).body
                ?: throw IllegalStateException()

        verify(shortUrlCreator, never()).create(urlToBeShortened, NULL_USER)
        verify(shortUrlRepository, times(1)).findByUrlAndUsernameNull(urlToBeShortened)
        verify(shortUrlRepository, never()).save(ShortUrl(urlToBeShortened, hashToBeAssumed, NULL_USER))
        verify(dataCollector, never()).collectVisit(any(), any(), any())

        assert.that(shortUrlResponse, present())
        assert.that(shortUrlResponse.url, equalTo(urlToBeShortened))
        assert.that(shortUrlResponse.shortUrl, endsWith(hashToBeAssumed))
    }

    @Test
    fun redirectFromHashShouldRedirectIfHashWasFound() {
        val hashToBeFound = "hashToBeFound"
        val redirectUrl = "http://www.google.de"
        val testUserAgent = "testUserAgent"

        whenever(shortUrlRepository.findByHash(eq(hashToBeFound))).thenReturn(ShortUrl(redirectUrl, hashToBeFound, NULL_USER))

        val response = mock<HttpServletResponse>()
        shortenController.redirectFromHash(hashToBeFound, response, testUserAgent)

        verify(response, times(1)).sendRedirect(redirectUrl)
        verify(response, never()).sendError(any())
        verify(dataCollector, times(1)).collectVisit(eq(ShortUrl(redirectUrl, hashToBeFound, NULL_USER)), eq(testUserAgent), any())
    }

    @Test
    fun redirectFromHashShouldReturn404WhenHashWasNotFound() {
        val hashNotToBeFound = "hashToBeFound"
        val testUserAgent = "testUserAgent"

        whenever(shortUrlRepository.findByHash(eq(hashNotToBeFound))).thenReturn(null)

        val response = mock<HttpServletResponse>()
        shortenController.redirectFromHash(hashNotToBeFound, response, testUserAgent)

        verify(response, never()).sendRedirect(any())
        verify(response, times(1)).sendError(404)
    }

    private fun creatorShouldReturnHashForUrl(hash: String, url: String) {
        whenever(shortUrlCreator.create(eq(url), eq(NULL_USER))).thenAnswer { invocation ->
            ShortUrl(invocation.arguments[0] as String, hash, NULL_USER)
        }
    }

    private fun configureRepositoryToReturnWhatWasSaved() {
        whenever(shortUrlRepository.save(any<ShortUrl>())).thenAnswer { invocation -> invocation.arguments[0] }
    }

    private fun repositoryShouldNotFindUrl() {
        whenever(shortUrlRepository.findByUrlAndUsernameNull(any())).thenReturn(null)
    }

    private fun repositoryShouldFindUrlWithHash(url: String, hash: String) {
        whenever(shortUrlRepository.findByUrlAndUsernameNull(eq(url))).thenAnswer { invocation ->
            ShortUrl(invocation.arguments[0] as String, hash, NULL_USER)
        }
    }
}