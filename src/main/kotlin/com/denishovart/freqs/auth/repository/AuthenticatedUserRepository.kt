package com.denishovart.freqs.auth.repository
import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.data.ReactiveRedisComponent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.LinkedHashMap

// FIXME make more more generic
@Repository
class AuthenticatedUserRepository(
    val reactiveRedisComponent: ReactiveRedisComponent,
    private val objectMapper: ObjectMapper
) {

    companion object {
        const val KEY = "AuthenticatedUser";
    }

    fun findById(id: String): Mono<AuthenticatedUser> {
        return reactiveRedisComponent
            .get(KEY, id)
            .flatMap {
            // For some reason this doesn't work (accessToken and refreshToken are empty on deserialization)
            // Mono.just(objectMapper.convertValue(it, AuthenticatedUser::class.java) )
                val value = it as LinkedHashMap<String, Any>
                Mono.just(AuthenticatedUser(
                    value["id"] as String,
                    value["provider"] as String,
                    value["attributes"] as LinkedHashMap<String, Any>,
                    (value["authorities"] as List<LinkedHashMap<String, Any>>).map {
                        SimpleGrantedAuthority(it["authority"] as String)
                    },
                    value["accessToken"] as String,
                    value["refreshToken"] as String?,
                ))
            }
    }

    fun save(authenticatedUser: AuthenticatedUser): Mono<AuthenticatedUser> {
        return reactiveRedisComponent.set(KEY, authenticatedUser.id, authenticatedUser).map { _ -> authenticatedUser }
    }

    fun remove(id: UUID): Mono<Void> {
        return reactiveRedisComponent.remove(KEY, id).then()
    }

}