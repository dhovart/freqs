package com.denishovart.freqs.playlist.dto

import com.denishovart.freqs.playlist.document.Track
import java.util.UUID

data class TrackAddedEvent(val playlistID: UUID, val track: Track)