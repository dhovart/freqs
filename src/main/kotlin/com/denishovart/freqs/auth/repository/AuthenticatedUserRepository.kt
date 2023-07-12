package com.denishovart.freqs.auth.repository
import com.denishovart.freqs.auth.entity.AuthenticatedUser
import com.denishovart.freqs.config.SecureSerializer
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
    private val reactiveRedisComponent: ReactiveRedisComponent,
    private val secureSerializer: SecureSerializer,
) {

    companion object {
        const val KEY = "AuthenticatedUser";
    }

    fun findById(id: String): Mono<AuthenticatedUser> {
        return reactiveRedisComponent
            .get(KEY, id)
            .flatMap {
                Mono.just(secureSerializer.decrypt(it as String, AuthenticatedUser::class.java))
            }
    }

    fun save(authenticatedUser: AuthenticatedUser): Mono<AuthenticatedUser> {
        return reactiveRedisComponent.set(KEY, authenticatedUser.id, secureSerializer.encrypt(authenticatedUser)).map { _ -> authenticatedUser }
    }

    fun remove(id: UUID): Mono<Void> {
        return reactiveRedisComponent.remove(KEY, id).then()
    }

}