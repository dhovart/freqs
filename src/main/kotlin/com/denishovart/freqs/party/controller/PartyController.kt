package com.denishovart.freqs.party.controller

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class PartyController  {

    @QueryMapping
    fun party(@Argument id: UUID) {
    }

    @MutationMapping
    fun createParty(@Argument name: String) {
    }

    @MutationMapping
    fun addTrack(@Argument partyID: UUID) {
    }

}