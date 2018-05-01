package io.gontrum.shorturl

import com.fasterxml.jackson.databind.ObjectMapper
import cucumber.api.java8.En
import io.gontrum.shorturl.model.ShortUrl
import io.gontrum.shorturl.model.ShortUrlRequest
import io.gontrum.shorturl.repository.ShortUrlRepository
import org.hamcrest.Matchers
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.util.Base64Utils

@SpringBootTest
@AutoConfigureMockMvc
class StepsDef : En {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var shortUrlRepository: ShortUrlRepository

    private lateinit var result: ResultActions

    init {
        When("^the client posts the url (.*) to (.*)$") { url: String, path: String ->
            result = mvc.perform(MockMvcRequestBuilders.post(path)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJsonString(ShortUrlRequest(url)))
            )
        }

        Then("^the client receives status code of (\\d+)$") { statusCode: Int ->
            result.andExpect(MockMvcResultMatchers.status().`is`(statusCode))
        }

        Then("^the client receives a shorturl$") {
            result.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").isNotEmpty())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.shortUrl").isString)
        }

        Then("^the client receives the url (.*)$") { url: String ->
            result.andExpect(MockMvcResultMatchers.jsonPath("$.url").exists())
            result.andExpect(MockMvcResultMatchers.jsonPath("$.url").isNotEmpty)
            result.andExpect(MockMvcResultMatchers.jsonPath("$.url").isString)
            result.andExpect(MockMvcResultMatchers.jsonPath("$.url", Matchers.equalTo(url)))
        }

        Then("^the client receives the error invalid url$") {
            result.andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
            result.andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty)
            result.andExpect(MockMvcResultMatchers.jsonPath("$.message").isString)
            result.andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("Validation failed. 1 error(s)")))
        }

        Given("^the url (.*) with the hash (.*)$") { url: String, hash: String ->
            shortUrlRepository.save(ShortUrl(url, hash, null))
        }

        When("^the client gets the url (.*)$") { path: String ->
            result = mvc.perform(MockMvcRequestBuilders.get(path)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
        }

        When("^the admin gets the url (.*)$") { path: String ->
            result = mvc.perform(MockMvcRequestBuilders.get(path)
                    .header(HttpHeaders.AUTHORIZATION,
                            "Basic " + Base64Utils.encodeToString("admin:admin".toByteArray()))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
        }

        When("^for (\\d+) times the client gets the url (.*)$") { times: Int, path: String ->
            (1..times).forEach {
                result = mvc.perform(MockMvcRequestBuilders.get(path)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
            }
        }

        Then("^the client receives a redirect to (.*)$") { url: String ->
            result.andExpect(MockMvcResultMatchers.redirectedUrl(url))
        }

        Then("^the client receives not found$") {
            result.andExpect(MockMvcResultMatchers.status().isNotFound)
        }

        When("^the client with the useragent (.*) gets the url (.*)$") { userAgent: String, url: String ->
            result = mvc.perform(MockMvcRequestBuilders.get(url)
                    .header("User-Agent", userAgent)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
        }

        Then("^the admin receives statistics with OS (.*) browser (.*) and invoces (\\d+)$") { os: String, browser: String, invoces: Int ->
            result.andExpect(MockMvcResultMatchers.jsonPath("$.hash").exists())
            result.andExpect(MockMvcResultMatchers.jsonPath("$.os['${os}']").exists())
            result.andExpect(MockMvcResultMatchers.jsonPath("$.browser['${browser}']").exists())
            result.andExpect(MockMvcResultMatchers.jsonPath("$.timesInvoced", Matchers.equalTo(invoces)))
        }

        Then("^the admin receives statistics containing hash (.*) on place (\\d+)$") { hash: String, place: Int ->
            result.andExpect(MockMvcResultMatchers.jsonPath("$.[${place}].hash", Matchers.equalTo(hash)))
        }

        Then("^the admin receives statistics containing (\\d+) items$") { numberOfItems: Int ->
            result.andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.equalTo(numberOfItems)))
        }
    }

    private fun asJsonString(obj: Any): String {
        try {
            val mapper = ObjectMapper()
            val jsonContent = mapper.writeValueAsString(obj)
            return jsonContent
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

}