package com.denishovart.freqs.auth.entity

import org.springframework.data.redis.core.RedisHash
import java.util.*

@RedisHash
data class AuthRequestHolder(
    val id: UUID,
    val payload: String
)