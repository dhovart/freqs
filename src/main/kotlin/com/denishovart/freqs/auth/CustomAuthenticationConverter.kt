package com.denishovart.freqs.auth

import com.denishovart.freqs.helper.getAuthId
import com.denishovart.freqs.auth.service.AuthenticatedUserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class CustomAuthenticationConverter(
    private @Value("\${encryption.password}") val encryptionPassword: String,
    private val authenticatedUserService: AuthenticatedUserService
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication?> {
        return Mono.justOrEmpty(exchange)
            .flatMap {
                Mono.justOrEmpty(it.getAuthId(encryptionPassword))
                    .flatMap { authId ->
                        authenticatedUserService.loadAuthenticatedUser(authId)
                            .flatMap { authenticatedUser ->
                                val authentication =
                                    UsernamePasswordAuthenticationToken(
                                        authenticatedUser,
                                        null,
                                        authenticatedUser.authorities
                                    )
                                ReactiveSecurityContextHolder.withAuthentication(authentication)
                                Mono.just(authentication)
                            }
                            .onErrorResume {
                                ReactiveSecurityContextHolder.clearContext()
                                Mono.empty()
                            }
                    }
            }
    }
}
