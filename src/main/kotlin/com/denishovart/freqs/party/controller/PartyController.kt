package com.denishovart.freqs.party.controller

import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.party.document.Party
import com.denishovart.freqs.party.service.PartyService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import java.util.UUID

@Controller
class PartyController(private val service: PartyService)  {

    @QueryMapping
    fun party(@Argument id: UUID) {
    }

    @MutationMapping
    fun createParty(@Argument name: String, @AuthenticationPrincipal user: AuthenticatedUser): Mono<Party> {
        return service.createParty(name, user.toUserEntity())
    }

    @MutationMapping
    fun addTrack(@Argument partyID: UUID) {
    }

}