package com.denishovart.freqs.auth

import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OAuth2AuthenticationFailureHandler(
    val logger: Logger
) : ServerAuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange,
        exception: AuthenticationException
    ): Mono<Void> {
        logger.error("In failure handler", exception)
        val response = webFilterExchange.exchange.response
        response.statusCode = HttpStatus.TEMPORARY_REDIRECT;
        response.headers.location = URI("/auth-error");

        return response.setComplete()
    }
}
