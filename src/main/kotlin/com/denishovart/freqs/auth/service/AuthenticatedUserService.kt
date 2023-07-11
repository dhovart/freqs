package com.denishovart.freqs.auth.service

import com.denishovart.freqs.api.entity.User
import com.denishovart.freqs.helper.Base64
import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.auth.repository.AuthenticatedUserRepository
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class AuthenticatedUserService(val repository: AuthenticatedUserRepository) {
    fun saveNewAuthenticatedUser(token: OAuth2AuthenticationToken): Mono<AuthenticatedUser> {
        val serializedToken = Base64.encrypt(token)
        val id = UUID.randomUUID()
        return repository.save(AuthenticatedUser(id, serializedToken))
    }

    fun loadAuthenticatedUser(id: UUID): Mono<AuthenticatedUser> {
        return repository.findById(id)
    }
}
