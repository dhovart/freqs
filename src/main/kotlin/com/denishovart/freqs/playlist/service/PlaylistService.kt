package com.denishovart.freqs.playlist.service

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.playlist.document.Playlist
import com.denishovart.freqs.playlist.document.Vote
import com.denishovart.freqs.playlist.dto.TrackInput
import com.denishovart.freqs.playlist.repository.PlaylistRepository
import com.denishovart.freqs.user.document.User
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class PlaylistService(
    private val repository: PlaylistRepository,
) {
    fun createPlaylist(name: String, user: User): Mono<Playlist> {
        val playlist = Playlist(name, user)
        return repository.save(playlist)
    }

    fun addTrackToPlaylist(playlistID: UUID, trackInput: TrackInput): Mono<Playlist> {
        return repository.findById(playlistID).flatMap { playlist ->
            ReactiveSecurityContextHolder.getContext().flatMap {
                val user = it.authentication.principal as AuthenticatedUser
                var track = trackInput.toTrack(submittedBy = user.toUserEntity())
                playlist.tracks.add(track)
                repository.save(playlist)
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
}