package com.denishovart.freqs.auth

import com.denishovart.freqs.config.SecureSerializer
import com.denishovart.freqs.helper.setAuthCookie
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

@Component
class OAuth2AuthenticationSuccessHandler(
    val logger: Logger,
    val secureSerializer: SecureSerializer,
    @Value("\${app.redirectToOnSuccessfulAuthentication}") val redirectUrl: String
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        logger.info("In success handler $authentication")
        val response = webFilterExchange.exchange.response
        response.statusCode = HttpStatus.TEMPORARY_REDIRECT
        response.headers.location = URI(redirectUrl)

        val id = "${(authentication as OAuth2AuthenticationToken).authorizedClientRegistrationId}_${authentication.name}"
        webFilterExchange.exchange.setAuthCookie(id, secureSerializer)
        return response.setComplete()
    }
}
