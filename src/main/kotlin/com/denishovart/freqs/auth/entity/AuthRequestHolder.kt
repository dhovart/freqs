package com.denishovart.freqs.auth.entity

import java.util.*

data class AuthRequestHolder(
    val id: UUID,
    val payload: String
)