package com.denishovart.freqs.auth.entity

import com.denishovart.freqs.api.entity.User
import org.springframework.data.redis.core.RedisHash
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import java.util.*

@RedisHash
data class AuthenticatedUser(
    val id: UUID,
    val serializedToken: String,
    val token: OAuth2AuthenticationToken? = null
) {
    fun toUserEntity(): User? {
        if (token == null) return null
        val oAuth2User = token.principal
        val id = (oAuth2User.getAttribute("id") as String?)!!
        val name = oAuth2User.getAttribute("display_name") as String?
        val picture = (oAuth2User.getAttribute("images") as ArrayList<LinkedHashMap<String, String>>?)?.get(0)?.get("url")
        return User(UUID.nameUUIDFromBytes(id.toByteArray()), name, picture)
    }
}