package com.denishovart.freqs.auth

import com.denishovart.freqs.auth.dto.RedirectStrategyPayload
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.web.server.ServerRedirectStrategy
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@Component
class JsonServerRedirectStrategy(
    @Qualifier("authObjectMapper")
    private val objectMapper: ObjectMapper,
    private val logger: Logger
) : ServerRedirectStrategy {

    override fun sendRedirect(exchange: ServerWebExchange, location: URI): Mono<Void> {
        logger.debug("Send redirect to $location")
        val payload = serializeRedirectStrategy(RedirectStrategyPayload(location.toString()))
        val buffer = exchange.response.bufferFactory().wrap(payload.toByteArray())
        exchange.response.statusCode = HttpStatus.OK
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return exchange.response.writeWith(Flux.just(buffer))
    }

    private fun serializeRedirectStrategy(redirectStrategyPayload: RedirectStrategyPayload): String {
        return objectMapper.writeValueAsString(redirectStrategyPayload)
    }
}