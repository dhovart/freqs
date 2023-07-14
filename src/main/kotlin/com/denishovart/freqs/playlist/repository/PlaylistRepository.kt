package com.denishovart.freqs.playlist.repository

import com.denishovart.freqs.playlist.document.Playlist
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*


@Repository
interface PlaylistRepository : ReactiveMongoRepository<Playlist, UUID> {
    @Query("{ id: { \$exists: true }}")
    fun findAllPartiesPaged(page: Pageable): Flux<Playlist?>?


}