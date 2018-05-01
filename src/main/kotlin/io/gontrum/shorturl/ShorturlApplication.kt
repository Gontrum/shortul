package io.gontrum.shorturl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShorturlApplication

fun main(args: Array<String>) {
    runApplication<ShorturlApplication>(*args)
}
