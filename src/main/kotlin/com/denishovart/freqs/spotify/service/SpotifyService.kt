package com.denishovart.freqs.spotify.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@Service
class SpotifyService(val webClient: WebClient) {
    fun search(query: String): Mono<Object> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/v1/search")
                    .queryParam("q", query)
                    .queryParam("type", "track")
                    .build()
            }
            .retrieve()
            .bodyToMono(Object::class.java)
    }
}