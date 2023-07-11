package com.denishovart.freqs.auth

import com.denishovart.freqs.auth.service.AuthenticatedUserService
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class CustomAuthorizedClientService(private val authenticatedUserService: AuthenticatedUserService) : ReactiveOAuth2AuthorizedClientService {

    override fun <T : OAuth2AuthorizedClient?> loadAuthorizedClient(
        clientRegistrationId: String?,
        principalName: String?
    ): Mono<T> {
        throw UnsupportedOperationException()
    }

    override fun removeAuthorizedClient(clientRegistrationId: String?, principalName: String?): Mono<Void>? {
        throw UnsupportedOperationException()
    }

    override fun saveAuthorizedClient(client: OAuth2AuthorizedClient?, authentication: Authentication): Mono<Void>? {
        return authenticatedUserService.saveNewAuthenticatedUser(authentication as OAuth2LoginAuthenticationToken).then()
    }
}