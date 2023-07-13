package com.denishovart.freqs.party.repository

import com.denishovart.freqs.party.document.Party
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*


@Repository
interface PartyRepository : ReactiveMongoRepository<Party, UUID> {
    @Query("{ id: { \$exists: true }}")
    fun findAllPartiesPaged(page: Pageable): Flux<Party?>?


}