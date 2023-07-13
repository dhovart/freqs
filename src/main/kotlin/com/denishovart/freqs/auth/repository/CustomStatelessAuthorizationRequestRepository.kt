package com.denishovart.freqs.auth.repository

import com.denishovart.freqs.auth.entity.AuthRequestHolder
import com.denishovart.freqs.config.SecureSerializer
import com.denishovart.freqs.helper.getAuthRequestCookie
import com.denishovart.freqs.helper.removeAuthRequestCookie
import com.denishovart.freqs.helper.setAuthRequestCookie
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@Component
class CustomStatelessAuthorizationRequestRepository(
    val authRequestHolderRepository: AuthRequestHolderRepository
) : ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    override fun loadAuthorizationRequest(exchange: ServerWebExchange): Mono<OAuth2AuthorizationRequest> {
        val authCookie = exchange.getAuthRequestCookie()
        val authId = authCookie?.value ?: return Mono.empty()
        return authRequestHolderRepository.findById(authId)
            .map { it.request }
    }

    override fun removeAuthorizationRequest(exchange: ServerWebExchange): Mono<OAuth2AuthorizationRequest> {
        val authCookie = exchange.getAuthRequestCookie()
        val authId = authCookie?.value ?: return Mono.empty()
        exchange.removeAuthRequestCookie()
        return authRequestHolderRepository.findById(authId)
            .flatMap { holder ->
                authRequestHolderRepository.remove(holder.id!!)
                    .thenReturn(holder.request!!)
            }
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        exchange: ServerWebExchange
    ): Mono<Void> {

        return if (authorizationRequest == null) {
            exchange.removeAuthRequestCookie()
            Mono.empty()
        } else {
            var authRequestHolder = AuthRequestHolder(
                UUID.randomUUID(),
                authorizationRequest
            )
            exchange.setAuthRequestCookie(authRequestHolder.id.toString())
            authRequestHolderRepository.save(authRequestHolder)
                .then()
        }
    }
}