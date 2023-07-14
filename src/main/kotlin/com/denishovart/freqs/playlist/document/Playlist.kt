package com.denishovart.freqs.playlist.document

import com.denishovart.freqs.base.document.BaseDocument
import com.denishovart.freqs.user.document.User
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Playlist(
    var name: String,
    var creator: User,
    var tracks: MutableList<Track> = mutableListOf()
): BaseDocument()