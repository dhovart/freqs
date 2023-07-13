package com.denishovart.freqs.party.service

import com.denishovart.freqs.party.document.Party
import com.denishovart.freqs.party.document.Vote
import com.denishovart.freqs.party.dto.TrackInput
import com.denishovart.freqs.party.repository.PartyRepository
import com.denishovart.freqs.user.document.User
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class PartyService(
    private val repository: PartyRepository,
) {
    fun createParty(name: String, user: User): Mono<Party> {
        val party = Party(name, user)
        return repository.save(party)
    }

    fun addTrackToParty(partyID: UUID, trackInput: TrackInput): Mono<Party> {
        return repository.findById(partyID).flatMap {
            it.tracks.add(trackInput.toTrack())
            repository.save(it)
        }
    }

    fun voteForTrack(partyID: UUID, trackID: UUID, user: User, comment: String?): Mono<Party> {
        return repository.findById(partyID).flatMap {
            it.tracks.find { it.id == trackID }?.votes?.add(Vote(issuer = user, comment = comment))
            repository.save(it)
        }
    }

    fun findParties(pageable: Pageable): Flux<Party?>? {
        return repository.findAllPartiesPaged(pageable)
    }

    fun findParty(id: UUID): Mono<Party> {
        return repository.findById(id)
    }
}