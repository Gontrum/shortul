package io.gontrum.shorturl.repository

import io.gontrum.shorturl.model.Statistic
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

interface StatisticsRepository : MongoRepository<Statistic, String>, StatisticsIncrementRepository {
    fun findByHash(hash: String): Statistic?
    fun findByHashAndUsername(hash: String, name: String): Statistic?

    fun findByUsername(username: String): Iterable<Statistic>
    fun findTop5ByAndUsernameOrderByTimesInvocedDesc(name: String): Iterable<Statistic>
    fun findTop5ByOrderByTimesInvocedDesc(): Iterable<Statistic>
}

interface StatisticsIncrementRepository {
    fun saveCall(hash: String, username: String?, browserName: String, os: String, calledAt: Date)
}

@Repository
class StatisticsIncrementRepositoryImpl(private val mongoTemplate: MongoTemplate) : StatisticsIncrementRepository {
    override fun saveCall(hash: String, username: String?, browserName: String, os: String, calledAt: Date) {
        val queryFindHash = Query(Criteria.where("hash").`is`(hash))
        val incrementBrowser = Update().inc("browser.${browserName}", 1)
        incrementBrowser.inc("os.${os}", 1)
        incrementBrowser.inc("timesInvoced", 1)
        incrementBrowser.addToSet("calledAt", calledAt)
        if (username != null) incrementBrowser.addToSet("username", username)
        mongoTemplate.upsert(queryFindHash, incrementBrowser, "statistics")
    }
}

