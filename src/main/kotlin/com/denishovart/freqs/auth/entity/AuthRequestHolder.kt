package com.denishovart.freqs.auth.entity

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import java.util.*

data class AuthRequestHolder(
    val id: UUID? = null,
    val request: OAuth2AuthorizationRequest? = null
)