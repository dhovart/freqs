package com.denishovart.freqs.spotify.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class SpotifyService {

    private val webClient: WebClient = WebClient.create("https://api.spotify.com")

    fun search(token: String, query: String): Mono<String> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/v1/search")
                    .queryParam("q", query)
                    .queryParam("type", "track")
                    .build()
            }
            .header("Authorization", "Bearer $token")
            .retrieve()
            .bodyToMono(String::class.java)
    }
}