package com.denishovart.freqs.spotify.controller

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.spotify.service.SpotifyService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono


@Controller
class SpotifyController(
    private val spotifyService: SpotifyService
) {

    @QueryMapping
    fun search(@Argument term: String): Mono<Object> {
        return spotifyService.search(term)
    }
}