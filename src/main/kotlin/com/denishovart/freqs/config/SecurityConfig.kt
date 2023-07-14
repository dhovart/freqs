package com.denishovart.freqs.config

import com.denishovart.freqs.auth.*
import com.denishovart.freqs.auth.filter.CustomRedirectFilter
import com.denishovart.freqs.auth.repository.CustomStatelessAuthorizationRequestRepository
import com.denishovart.freqs.auth.service.AuthorizedClientService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    val customStatelessAuthorizationRequestRepository: CustomStatelessAuthorizationRequestRepository,
    val customStatelessAuthorizationRequestResolver: CustomStatelessAuthorizationRequestResolver,
    val authorizedClientService: AuthorizedClientService,
    val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
    val customRedirectFilter: CustomRedirectFilter,
    private val authenticationConverter: CustomAuthenticationConverter,
) {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowCredentials = true
        configuration.allowedOriginPatterns = listOf("*") // FIXME
//        configuration.allowedOrigins = mutableListOf("http://blah")
        configuration.allowedMethods = mutableListOf("*")
        configuration.allowedHeaders = mutableListOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
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
            .cors(Customizer.withDefaults())
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeExchange {
                it.pathMatchers("/login/oauth2/code/**").permitAll()
                it.anyExchange().authenticated()
            }
            .oauth2Login {
                it.authorizationRequestRepository(customStatelessAuthorizationRequestRepository)
                it.authorizationRequestResolver(customStatelessAuthorizationRequestResolver)
                it.authorizedClientService(authorizedClientService)
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
