package com.denishovart.freqs.playlist.dto

import com.denishovart.freqs.user.document.User
import java.util.*

data class TrackMovedEvent(
    val playlistID: UUID,
    val trackID: UUID,
    val oldPosition: Int,
    val newPosition: Int,
    val user: User,
)