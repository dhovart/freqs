package com.denishovart.freqs.config

import com.denishovart.freqs.base.repository.BaseMongoRepositoryImpl
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.UuidRepresentation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(basePackages = ["com.denishovart.freqs"], repositoryBaseClass = BaseMongoRepositoryImpl::class)
class MongoReactiveApplication() : AbstractReactiveMongoConfiguration() {
    @Bean
    fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://localhost:27017/$databaseName") // FIXME
        val mongoClientSettings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(connectionString).build()
        return MongoClients.create(mongoClientSettings)
    }

    override fun getDatabaseName(): String {
        return "freqs"
    }
}