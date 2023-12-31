package com.denishovart.freqs.playlist.controller

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.playlist.document.Playlist
import com.denishovart.freqs.playlist.dto.TrackAddedEvent
import com.denishovart.freqs.playlist.dto.TrackInput
import com.denishovart.freqs.playlist.dto.TrackMovedEvent
import com.denishovart.freqs.playlist.service.PlaylistService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.*

@Controller
class PlaylistController(private val service: PlaylistService) {

    @QueryMapping
    fun playlists(@Argument page: Int, @Argument size: Int): Flux<Playlist?>? {
        val pageable: Pageable = PageRequest.of(page, minOf(size, 50))
        return service.findPlaylists(pageable)
    }

    @QueryMapping
    fun playlist(@Argument id: UUID): Mono<Playlist> {
        return service.findPlaylist(id)
    }

    @MutationMapping
    fun createPlaylist(
        @Argument name: String,
        @Argument tracks: List<TrackInput>?,
        @AuthenticationPrincipal user: AuthenticatedUser
    ): Mono<Playlist> {
        return service.createPlaylist(
            name ?: "Untitled Playlist",
            user.toUserEntity(),
            tracks ?: listOf()
        )
    }

    @MutationMapping
    fun addTrack(@Argument playlistID: UUID, @Argument track: TrackInput): Mono<Playlist> {
        return service.addTrackToPlaylist(playlistID, track)
    }

    @MutationMapping
    fun updatePlaylistName(@Argument playlistID: UUID, @Argument name: String): Mono<Playlist> {
        return service.updatePlaylistName(playlistID, name)
    }

    @SubscriptionMapping
    fun trackAdded(@Argument playlistID: UUID): Flux<TrackAddedEvent> {
        return service.getTrackAddedPublisher().filter { it.playlistID == playlistID }
    }

    @MutationMapping
    fun voteForTrack(
        @Argument playlistID: UUID,
        @Argument trackID: UUID,
        @Argument comment: String?,
        @AuthenticationPrincipal user: AuthenticatedUser
    ): Mono<Playlist> {
        return service.voteForTrack(playlistID, trackID, user.toUserEntity(), comment)
    }

    @MutationMapping
    fun updatePlaylistTrackPosition(
        @Argument playlistID: UUID,
        @Argument trackID: UUID,
        @Argument newPosition: Int
    ): Mono<Playlist> {
        return service.movePlaylistTrack(
            playlistID,
            trackID,
            newPosition
        )
    }

    @SubscriptionMapping
    fun trackMoved(@Argument playlistID: UUID): Flux<TrackMovedEvent> {
        return service.getTrackMovedPublisher().filter { it.playlistID == playlistID }
    }


}