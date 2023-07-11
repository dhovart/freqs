package com.denishovart.freqs.auth.service

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.auth.repository.AuthenticatedUserRepository
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticatedUserService(val repository: AuthenticatedUserRepository) {
    fun saveNewAuthenticatedUser(token: OAuth2LoginAuthenticationToken): Mono<AuthenticatedUser> {
        val id = "${token.clientRegistration.registrationId}_${token.name}"
        return repository.save(AuthenticatedUser(
            id,
            token.clientRegistration.registrationId,
            token.principal.attributes,
            token.principal.authorities,
            token.accessToken.tokenValue,
            token.refreshToken?.tokenValue
        ))
    }

    fun loadAuthenticatedUser(id: String): Mono<AuthenticatedUser> {
        return repository.findById(id)
    }
}
