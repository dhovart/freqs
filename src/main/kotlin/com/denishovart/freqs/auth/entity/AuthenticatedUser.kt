package com.denishovart.freqs.auth.entity

import com.denishovart.freqs.user.document.User
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import java.util.*


data class AuthenticatedUser(
    val id: String = "",
    val client: OAuth2AuthorizedClient? = null,
    val authorities: Collection<GrantedAuthority> = listOf(),
    val attributes: Map<String, Any> = LinkedHashMap(),
) {
    fun toUserEntity(): User {
        val name = attributes["display_name"] as String
        val picture = (attributes["images"] as ArrayList<LinkedHashMap<String, String>>?)?.get(0)?.get("url")
        return User(UUID.nameUUIDFromBytes(id.toByteArray()), name, picture)
    }
}