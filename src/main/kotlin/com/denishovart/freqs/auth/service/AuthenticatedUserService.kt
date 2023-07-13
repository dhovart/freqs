package com.denishovart.freqs.auth.service

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.auth.repository.AuthenticatedUserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticatedUserService(val repository: AuthenticatedUserRepository) {
    fun saveAuthenticatedUserUsingOAuth2Authentication(client: OAuth2AuthorizedClient, token: OAuth2AuthenticationToken): Mono<AuthenticatedUser> {
        return saveUser(
            id = "${token.authorizedClientRegistrationId}:${token.name}",
            name = token.name,
            client = client,
            authorities = token.authorities,
            attributes = token.principal.attributes,
        )
    }
    fun saveAuthenticatedUserUsingOAuth2LoginAuthentication(client: OAuth2AuthorizedClient, token: OAuth2LoginAuthenticationToken): Mono<AuthenticatedUser> {
        return saveUser(
            id = "${token.clientRegistration.registrationId}:${token.name}",
            name = token.name,
            client = client,
            authorities = token.authorities,
            attributes = token.principal.attributes,
        )
    }

    private fun saveUser(
        id: String,
        name: String,
        client: OAuth2AuthorizedClient,
        authorities: MutableCollection<GrantedAuthority>,
        attributes: MutableMap<String, Any>
    ): Mono<AuthenticatedUser> {
        return repository.save(AuthenticatedUser(
            id,
            name,
            authorities,
            attributes,
            client,
        ))
    }

    fun loadAuthenticatedUser(id: String): Mono<AuthenticatedUser> {
        return repository.findById(id)
    }
}
