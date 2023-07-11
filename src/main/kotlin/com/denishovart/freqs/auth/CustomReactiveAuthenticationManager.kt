package com.denishovart.freqs.auth

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CustomReactiveAuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
    }
}