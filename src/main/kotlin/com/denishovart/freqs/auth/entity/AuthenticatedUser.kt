package com.denishovart.freqs.auth.entity

import com.denishovart.freqs.user.document.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.*


data class AuthenticatedUser(
    val id: String = "",
    private val name: String = "",
    private val authorities: MutableCollection<out GrantedAuthority> = mutableListOf(),
    private val attributes: MutableMap<String, Any> = mutableMapOf(),
    val client: OAuth2AuthorizedClient? = null,
): OAuth2User {
    fun toUserEntity(): User {
        val name = attributes["display_name"] as String
        val picture = (attributes["images"] as ArrayList<LinkedHashMap<String, String>>?)?.get(0)?.get("url")
        return User(UUID.nameUUIDFromBytes(id.toByteArray()), name, picture)
    }

    override fun getName(): String {
        return name
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }
}