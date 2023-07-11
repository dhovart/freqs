package com.denishovart.freqs.config

import com.denishovart.freqs.auth.*
import com.denishovart.freqs.auth.filter.CustomRedirectFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    val customStatelessAuthorizationRequestRepository: CustomStatelessAuthorizationRequestRepository,
    val customStatelessAuthorizationRequestResolver: CustomStatelessAuthorizationRequestResolver,
    val customAuthorizedClientService: CustomAuthorizedClientService,
    val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
    val customRedirectFilter: CustomRedirectFilter,
    private val authenticationConverter: CustomAuthenticationConverter,
) {
    @Bean
    fun authenticationManager(): ReactiveAuthenticationManager? {
        return ReactiveAuthenticationManager {
            authentication: Authentication -> Mono.just(authentication)
        }
    }

    @Throws(Exception::class)
    fun authenticationWebFilter(): AuthenticationWebFilter? {
        val filter = AuthenticationWebFilter(authenticationManager())
        filter.setServerAuthenticationConverter(authenticationConverter)
        return filter
    }

    @Bean
    fun securityWebFilterChain(httpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        return httpSecurity
            .csrf { it.disable() }
            .cors { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeExchange {
                it.pathMatchers("/login/oauth2/code/**").permitAll()
                it.anyExchange().authenticated()
            }
            .oauth2Login {
                it.authorizationRequestRepository(customStatelessAuthorizationRequestRepository)
                it.authorizationRequestResolver(customStatelessAuthorizationRequestResolver)
                it.authorizedClientService(customAuthorizedClientService)
                it.authenticationSuccessHandler(oAuth2AuthenticationSuccessHandler)
                it.authenticationFailureHandler(oAuth2AuthenticationFailureHandler)
            }
            .exceptionHandling {
                it.accessDeniedHandler { exchange, _ ->
                    exchange.response.statusCode = HttpStatusCode.valueOf(401)
                    Mono.empty<Void>()
                }
                it.authenticationEntryPoint { exchange, _ ->
                    exchange.response.statusCode = HttpStatusCode.valueOf(401)
                    Mono.empty<Void>()
                }
            }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .addFilterAt(customRedirectFilter, SecurityWebFiltersOrder.HTTP_BASIC)
            .addFilterBefore(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}
