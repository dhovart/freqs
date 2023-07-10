package com.denishovart.freqs.auth.entity

import org.springframework.data.redis.core.RedisHash
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import java.util.*

@RedisHash
data class AuthenticatedUser(
    val id: UUID,
    val serializedToken: String,
    val token: OAuth2AuthenticationToken? = null
)