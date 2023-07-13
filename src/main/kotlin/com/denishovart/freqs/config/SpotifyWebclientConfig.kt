package com.denishovart.freqs.config

import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import java.util.function.Consumer


@Configuration
class SpotifyWebClientConfig(
    private val oAuth2AuthorizedClientProvider: ReactiveOAuth2AuthorizedClientProvider,
    private val logger: Logger
) {
    @Bean
    fun webClient(clientRegistrations: ReactiveClientRegistrationRepository, authorizedClients: ServerOAuth2AuthorizedClientRepository): WebClient {
        val oauth = ServerOAuth2AuthorizedClientExchangeFilterFunction(
            clientRegistrations,
            authorizedClients
        )
        oauth.setDefaultOAuth2AuthorizedClient(true)
        oauth.setDefaultClientRegistrationId("spotify")
        return WebClient.builder()
            .baseUrl("https://api.spotify.com")
            .filter(oauth)
            .filter(loggingRequest())
            .filter(loggingResponse())
            .build()

    }

    private fun loggingRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest: ClientRequest, next: ExchangeFunction ->
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url())
            clientRequest.headers()
                .forEach { name: String?, values: List<String?> ->
                    values.forEach(
                        Consumer<String?> { value: String? ->
                            logger.info(
                                "{}={}",
                                name,
                                value
                            )
                        })
                }
            next.exchange(clientRequest)
        }
    }

    private fun loggingResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { clientResponse: ClientResponse ->
            logger.info("Response status: {}", clientResponse.statusCode())
            Mono.just<ClientResponse>(clientResponse)
        }
    }

}