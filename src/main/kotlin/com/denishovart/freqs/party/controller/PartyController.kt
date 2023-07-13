package com.denishovart.freqs.party.controller

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.party.document.Party
import com.denishovart.freqs.party.document.Track
import com.denishovart.freqs.party.dto.TrackInput
import com.denishovart.freqs.party.service.PartyService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Controller
class PartyController(private val service: PartyService) {

    @QueryMapping
    fun parties(@Argument page: Int, @Argument size: Int): Flux<Party?>? {
        val pageable: Pageable = PageRequest.of(page, minOf(size, 50))
        return service.findParties(pageable)
    }

    @QueryMapping
    fun party(@Argument id: UUID): Mono<Party> {
        return service.findParty(id)
    }

    @MutationMapping
    fun createParty(@Argument name: String, @AuthenticationPrincipal user: AuthenticatedUser): Mono<Party> {
        return service.createParty(name, user.toUserEntity())
    }

    @MutationMapping
    fun addTrack(@Argument partyID: UUID, @Argument track: TrackInput): Mono<Party> {
        return service.addTrackToParty(partyID, track)
    }

    @MutationMapping
    fun voteForTrack(
        @Argument partyID: UUID,
        @Argument trackID: UUID,
        @Argument comment: String?,
        @AuthenticationPrincipal user: AuthenticatedUser
    ): Mono<Party> {
        return service.voteForTrack(partyID, trackID, user.toUserEntity(), comment)
    }


}