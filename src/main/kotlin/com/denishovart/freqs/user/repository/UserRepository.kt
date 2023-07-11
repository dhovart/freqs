package com.denishovart.freqs.user.repository

import com.denishovart.freqs.user.document.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : ReactiveCrudRepository<User?, UUID?>