package com.denishovart.freqs.auth.repository

import com.denishovart.freqs.auth.entity.AuthRequestHolder
import com.denishovart.freqs.data.ReactiveRedisComponent
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.LinkedHashMap

@Repository
class AuthRequestHolderRepository(val reactiveRedisComponent: ReactiveRedisComponent) {

    companion object {
        const val KEY = "AuthRequestHolder"
    }

    fun findById(id: UUID): Mono<AuthRequestHolder> {
        return reactiveRedisComponent
            .get(KEY, id)
            .map { it as LinkedHashMap<*, *> }
            .map { data ->
                val id = UUID.fromString(data["id"] as String)
                val payload = data["payload"] as String
                AuthRequestHolder(id, payload)
            }
    }

    fun save(authRequestHolder: AuthRequestHolder): Mono<AuthRequestHolder> {
        return reactiveRedisComponent.set(KEY, authRequestHolder.id.toString(), authRequestHolder).map { _ -> authRequestHolder }
    }

    fun remove(id: UUID): Mono<Void> {
        return reactiveRedisComponent.remove(KEY, id).then()
    }
}