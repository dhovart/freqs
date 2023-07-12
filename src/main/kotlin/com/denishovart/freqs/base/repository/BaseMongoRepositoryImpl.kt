package com.denishovart.freqs.base.repository

import com.denishovart.freqs.base.document.BaseDocument
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository
import reactor.core.publisher.Mono
import java.util.*

class BaseMongoRepositoryImpl<T : BaseDocument>(
    entityInformation: MongoEntityInformation<T, UUID>,
    mongoOperations: ReactiveMongoOperations
) : SimpleReactiveMongoRepository<T, UUID>(
    entityInformation,
    mongoOperations
), BaseMongoRepository<T> {
    override fun <S : T> save(entity: S): Mono<S> {
        if (entity.id == null) {
            entity.id = UUID.randomUUID();
        }
        if (entity.createdAt == null) {
            entity.createdAt = Date()
        }
        return super.save(entity);
    }

}