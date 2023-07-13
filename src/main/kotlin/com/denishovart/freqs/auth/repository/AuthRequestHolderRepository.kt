package com.denishovart.freqs.auth.repository

import com.denishovart.freqs.auth.entity.AuthRequestHolder
import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.config.SecureSerializer
import com.denishovart.freqs.data.ReactiveRedisComponent
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
class AuthRequestHolderRepository(
    private val reactiveRedisComponent: ReactiveRedisComponent,
    private val secureSerializer: SecureSerializer
) {

    companion object {
        const val KEY = "AuthRequestHolder"
    }

    fun findById(id: String): Mono<AuthRequestHolder> {
        return reactiveRedisComponent
            .get(KEY, id)
            .flatMap {
                Mono.just(it as AuthRequestHolder)
            }
    }

    fun save(authRequestHolder: AuthRequestHolder): Mono<AuthRequestHolder> {
        return reactiveRedisComponent.set(KEY, authRequestHolder.id.toString(), authRequestHolder).map { _ -> authRequestHolder }
    }

    fun remove(id: UUID): Mono<Void> {
        return reactiveRedisComponent.remove(KEY, id).then()
    }
}