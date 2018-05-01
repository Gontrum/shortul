package io.gontrum.shorturl

import com.mongodb.MongoClient
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import java.io.IOException

@Configuration
@Profile("container")
class MongoDbConfig {
    @Value("\${mongodb.host}")
    private lateinit var mongoHost: String
    @Value("\${mongodb.name}")
    private lateinit var mongoName: String

    @Bean
    fun mongoDbFactory() = SimpleMongoDbFactory(MongoClient(mongoHost), mongoName)
}

@Configuration
@Profile("testing")
@Import(EmbeddedMongoAutoConfiguration::class)
class EmbeddedMongoConfig {
    private val MONGO_DB_URL = "localhost"
    private val MONGO_DB_NAME = "embeded_db"

    @Bean
    @Throws(IOException::class)
    fun mongoTemplate(): MongoTemplate {
        val mongo = EmbeddedMongoFactoryBean()
        mongo.setBindIp(MONGO_DB_URL)
        val mongoClient = mongo.`object` ?: throw IllegalStateException("mongoClient could not be initialized")
        val mongoTemplate = MongoTemplate(mongoClient, MONGO_DB_NAME)
        return mongoTemplate
    }

}
