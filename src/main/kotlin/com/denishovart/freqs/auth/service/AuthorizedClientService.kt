package com.denishovart.freqs.auth.service

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.auth.repository.AuthenticatedUserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthorizedClientService(
    private val repository: AuthenticatedUserRepository
) : ReactiveOAuth2AuthorizedClientService {
    override fun <T : OAuth2AuthorizedClient?> loadAuthorizedClient(
        clientRegistrationId: String?,
        principalName: String?
    ): Mono<T> {
        return loadAuthorizedUser("${clientRegistrationId}:$principalName")!!.map {
            it.client!! as T
        }
    }

    fun loadAuthorizedUser(id: String): Mono<AuthenticatedUser> {
        return repository.findById(id)
    }

    override fun removeAuthorizedClient(clientRegistrationId: String?, principalName: String?): Mono<Void>? {
        throw UnsupportedOperationException()
    }

    override fun saveAuthorizedClient(client: OAuth2AuthorizedClient, authentication: Authentication): Mono<Void>? {
        when (authentication) {
            is OAuth2LoginAuthenticationToken ->
                return saveAuthenticatedUserUsingOAuth2LoginAuthentication(client, authentication)
                    .then()

            is OAuth2AuthenticationToken ->
                return saveAuthenticatedUserUsingOAuth2Authentication(client, authentication).then()
        }
        return Mono.empty()
    }

    private fun saveAuthenticatedUserUsingOAuth2Authentication(
        client: OAuth2AuthorizedClient,
        token: OAuth2AuthenticationToken
    ): Mono<AuthenticatedUser> {
        return saveUser(
            id = "${token.authorizedClientRegistrationId}:${token.name}",
            name = token.name,
            client = client,
            authorities = token.authorities,
            attributes = token.principal.attributes,
        )
    }

    private fun saveAuthenticatedUserUsingOAuth2LoginAuthentication(
        client: OAuth2AuthorizedClient,
        token: OAuth2LoginAuthenticationToken
    ): Mono<AuthenticatedUser> {
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
        return repository.save(
            AuthenticatedUser(
                id,
                name,
                authorities,
                attributes,
                client,
            )
        )
    }
}