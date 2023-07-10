package com.denishovart.freqs.auth.repository
import com.denishovart.freqs.data.ReactiveRedisComponent
import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.helper.Base64
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.LinkedHashMap

// FIXME make more more generic
@Repository
class AuthenticatedUserRepository(val reactiveRedisComponent: ReactiveRedisComponent) {

    companion object {
        const val KEY = "AuthenticatedUser";
    }

    private fun mapProperties(hashMap: Mono<LinkedHashMap<*, *>>): Mono<AuthenticatedUser> {
        return hashMap.map { data ->
            val id = UUID.fromString(data["id"] as String)
            val serializedToken = data["serializedToken"] as String
            var token = Base64.decrypt<OAuth2AuthenticationToken>(serializedToken)
            AuthenticatedUser(id, serializedToken, token)
        }
    }

    fun findById(id: UUID): Mono<AuthenticatedUser> {
        return reactiveRedisComponent
            .get(KEY, id)
            .flatMap { mapProperties(Mono.just(it as LinkedHashMap<*, *>)) }
    }

    fun save(authenticatedUser: AuthenticatedUser): Mono<AuthenticatedUser> {
        return reactiveRedisComponent.set(KEY, authenticatedUser.id.toString(), authenticatedUser).map { _ -> authenticatedUser }
    }

    fun remove(id: UUID): Mono<Void> {
        reactiveRedisComponent.remove(KEY, id)
        return Mono.empty()
    }

}