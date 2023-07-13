package com.denishovart.freqs.auth.entity

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

data class AuthRequestHolder(
    val id: UUID? = null,
    val payload: String = ""
)