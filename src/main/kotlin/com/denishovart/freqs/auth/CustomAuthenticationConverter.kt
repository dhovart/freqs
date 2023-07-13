package com.denishovart.freqs.auth

import com.denishovart.freqs.auth.service.AuthenticatedUserService
import com.denishovart.freqs.config.SecureSerializer
import com.denishovart.freqs.helper.getAuthId
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class CustomAuthenticationConverter(
    private val authenticatedUserService: AuthenticatedUserService,
    private val secureSerializer: SecureSerializer
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication?> {
        return Mono.justOrEmpty(exchange)
            .flatMap {
                Mono.justOrEmpty(it.getAuthId(secureSerializer))
                    .flatMap { authId ->
                        authenticatedUserService.loadAuthenticatedUser(authId)
                            .flatMap { authenticatedUser ->
                                val authentication =
                                    OAuth2AuthenticationToken(
                                        authenticatedUser,
                                        authenticatedUser.authorities,
                                        authenticatedUser.client!!.clientRegistration.registrationId
                                    )
                                Mono.just(authentication)
                            }
                            .onErrorResume {
                                Mono.empty()
                            }
                    }
            }
    }
}
