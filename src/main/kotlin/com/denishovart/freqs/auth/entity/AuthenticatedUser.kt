package com.denishovart.freqs.auth.entity

import com.denishovart.freqs.user.document.User
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.data.redis.core.RedisHash
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken
import java.util.*
import kotlin.collections.LinkedHashMap

@RedisHash
data class AuthenticatedUser(
    val id: String = "",
    val provider: String = "",
    val attributes: Map<String, Any> = LinkedHashMap(),
    val authorities: Collection<GrantedAuthority> = listOf(),
    val accessToken: String = "",
    val refreshToken: String? = ""
) {
    fun toUserEntity(): User? {
        val name = attributes["display_name"] as String
        val picture = (attributes["images"] as ArrayList<LinkedHashMap<String, String>>?)?.get(0)?.get("url")
        return User(UUID.nameUUIDFromBytes(id.toByteArray()), name, picture)
    }
}