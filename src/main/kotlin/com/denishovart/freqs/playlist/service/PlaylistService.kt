package com.denishovart.freqs.playlist.service

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.playlist.document.Playlist
import com.denishovart.freqs.playlist.document.Vote
import com.denishovart.freqs.playlist.dto.TrackAddedEvent
import com.denishovart.freqs.playlist.dto.TrackInput
import com.denishovart.freqs.playlist.repository.PlaylistRepository
import com.denishovart.freqs.user.document.User
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.util.concurrent.Queues
import java.util.*

@Service
class PlaylistService(
    private val repository: PlaylistRepository,
) {
    private val trackAddedSink = Sinks.many().multicast().onBackpressureBuffer<TrackAddedEvent>(Queues.SMALL_BUFFER_SIZE, false)

    fun getTrackAddedPublisher(): Flux<TrackAddedEvent> {
        return trackAddedSink.asFlux()
    }

    fun createPlaylist(name: String, user: User, tracks: List<TrackInput> = listOf()): Mono<Playlist> {
        val playlist = Playlist(name, user, tracks.map { it.toTrack(submittedBy = user) }.toMutableList())
        return repository.save(playlist)
    }

    fun addTrackToPlaylist(playlistID: UUID, trackInput: TrackInput): Mono<Playlist> {
        return repository.findById(playlistID).flatMap { playlist ->
            ReactiveSecurityContextHolder.getContext().flatMap {
                val user = it.authentication.principal as AuthenticatedUser
                var track = trackInput.toTrack(submittedBy = user.toUserEntity())
                playlist.tracks.add(track)
                repository.save(playlist).doOnNext {
                    trackAddedSink.tryEmitNext(TrackAddedEvent(it.id!!, track))
                }
            }
        }
    }

    fun voteForTrack(playlistID: UUID, trackID: UUID, user: User, comment: String?): Mono<Playlist> {
        return repository.findById(playlistID).flatMap {
            it.tracks.find { it.id == trackID }?.votes?.add(Vote(issuer = user, comment = comment))
            repository.save(it)
        }
    }

    fun findPlaylists(pageable: Pageable): Flux<Playlist?>? {
        return repository.findAllPartiesPaged(pageable)
    }

    fun findPlaylist(id: UUID): Mono<Playlist> {
        return repository.findById(id)
    }

    fun updatePlaylistName(playlistID: UUID, name: String): Mono<Playlist> {
        return repository.findById(playlistID).flatMap {
            it.name = name
            repository.save(it)
        }
    }
}