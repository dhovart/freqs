package com.denishovart.freqs.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoRepositories
class MongoReactiveApplication(
    @Value("\${database.name}") databaseName: String
) : AbstractReactiveMongoConfiguration() {
    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create()
    }

    override fun getDatabaseName(): String {
        return databaseName
    }
}