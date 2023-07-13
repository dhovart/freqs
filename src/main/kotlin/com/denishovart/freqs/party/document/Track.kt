package com.denishovart.freqs.party.document

import com.denishovart.freqs.base.document.BaseDocument

data class Track(
    val name: String,
    val albumName: String,
    val image: String?,
    val duration: Float,
    val spotifyId: String,
    val spotifyAlbumId: String,
    val artists: MutableList<Artist>,
    val votes: MutableList<Vote>
): BaseDocument()