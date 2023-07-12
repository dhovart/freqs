package com.denishovart.freqs.party.service

import com.denishovart.freqs.party.document.Party
import com.denishovart.freqs.party.repository.PartyRepository
import com.denishovart.freqs.user.document.User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PartyService(private val repository: PartyRepository) {
    fun createParty(name: String, user: User): Mono<Party> {
        val party = Party(name, user)
        return repository.save(party)
    }
}