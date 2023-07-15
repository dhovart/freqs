package com.denishovart.freqs.playlist.dto

import com.denishovart.freqs.playlist.document.Artist
import com.denishovart.freqs.playlist.document.Track
import com.denishovart.freqs.user.document.User
import java.util.UUID

data class TrackInput(
    val name: String,
    val albumName: String,
    val image: String?,
    val duration: Int,
    val spotifyId: String,
    val spotifyAlbumId: String,
    val artists: List<Artist>
) {
    fun toTrack(submittedBy: User): Track {
        val track = Track(
            name,
            albumName,
            image,
            duration,
            spotifyId,
            spotifyAlbumId,
            artists.toMutableList(),
            mutableListOf(),
            submittedBy
        )
        track.id = UUID.randomUUID()
        return track
    }
}