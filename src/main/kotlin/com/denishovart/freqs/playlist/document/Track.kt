package com.denishovart.freqs.playlist.document

import com.denishovart.freqs.base.document.BaseDocument
import com.denishovart.freqs.user.document.User

data class Track(
    var name: String,
    val albumName: String,
    val image: String?,
    val duration: Int,
    val spotifyId: String,
    val spotifyAlbumId: String,
    val artists: MutableList<Artist>,
    val votes: MutableList<Vote>,
    var submittedBy: User,
): BaseDocument()