package com.denishovart.freqs.party.repository

import com.denishovart.freqs.party.document.Party
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PartyRepository : ReactiveMongoRepository<Party, UUID>