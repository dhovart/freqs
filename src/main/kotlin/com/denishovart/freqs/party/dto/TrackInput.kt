package com.denishovart.freqs.party.dto

import com.denishovart.freqs.party.document.Artist
import com.denishovart.freqs.party.document.Track
import java.util.UUID

data class TrackInput(
    val name: String,
    val albumName: String,
    val image: String?,
    val duration: Float,
    val spotifyId: String,
    val spotifyAlbumId: String,
    val artists: List<Artist>
) {
    fun toTrack(): Track {
        val track = Track(
            name,
            albumName,
            image,
            duration,
            spotifyId,
            spotifyAlbumId,
            artists.toMutableList(),
            mutableListOf()
        )
        track.id = UUID.randomUUID()
        return track
    }
}