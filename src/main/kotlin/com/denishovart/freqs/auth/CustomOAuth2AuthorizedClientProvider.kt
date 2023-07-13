package com.denishovart.freqs.auth

import org.springframework.security.oauth2.client.OAuth2AuthorizationContext
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class CustomOAuth2AuthorizedClientProvider() : ReactiveOAuth2AuthorizedClientProvider {

    override fun authorize(context: OAuth2AuthorizationContext): Mono<OAuth2AuthorizedClient> {
        val authentication = context.principal as? OAuth2AuthenticationToken

        if (authentication != null) {
            val authorizedClient = context.authorizedClient
            val refreshToken = authorizedClient?.refreshToken
            val accessToken = authorizedClient?.accessToken

            if (accessToken != null && accessToken.expiresSoon()) {
                return refreshTokenIfAvailable(context.clientRegistration, refreshToken, authentication)
            }
        }

        return Mono.justOrEmpty(context?.authorizedClient)
    }

    private fun refreshTokenIfAvailable(clientRegistration: ClientRegistration, refreshToken: OAuth2RefreshToken?, authentication: OAuth2AuthenticationToken): Mono<OAuth2AuthorizedClient> {
        if (refreshToken != null) {
            val tokenRequest = WebClient.create(clientRegistration.providerDetails.tokenUri)
                .post()
                .uri { it.path("/oauth/token").build() }
                .header("Authorization", "Basic " + getAuthorizationHeader(clientRegistration))
                .bodyValue(getRefreshTokenRequestBody(refreshToken))
                .retrieve()
                .bodyToMono(OAuth2AccessTokenResponse::class.java)

            return tokenRequest.map { response ->
                OAuth2AuthorizedClient(clientRegistration, authentication.name, response.accessToken, refreshToken)
            }
        }

        return Mono.empty()
    }

    private fun getAuthorizationHeader(clientRegistration: ClientRegistration): String {
        val clientId = clientRegistration.clientId
        val clientSecret = clientRegistration.clientSecret
        val credentials = "$clientId:$clientSecret"
        return Base64.getEncoder().encodeToString(credentials.toByteArray(StandardCharsets.UTF_8))
    }

    private fun getRefreshTokenRequestBody(refreshToken: OAuth2RefreshToken): MultiValueMap<String, String> {
        val body = LinkedMultiValueMap<String, String>()
        body.add("grant_type", "refresh_token")
        body.add("refresh_token", refreshToken.tokenValue)
        return body
    }
}

fun OAuth2AccessToken.expiresSoon(): Boolean {
    val expiration = this.expiresAt
    if (expiration != null) {
        val currentTime = Instant.now()
        val expiresIn = Duration.between(currentTime, expiration)
        val expiresInMinutes = expiresIn.toMinutes()
        return expiresInMinutes < 5
    }
    return false
}
