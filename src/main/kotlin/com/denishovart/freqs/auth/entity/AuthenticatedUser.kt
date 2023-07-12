package com.denishovart.freqs.auth.entity

import com.denishovart.freqs.user.document.User
import org.springframework.security.core.GrantedAuthority
import java.util.*


data class AuthenticatedUser(
    val id: String = "",
    val provider: String = "",
    val attributes: Map<String, Any> = LinkedHashMap(),
    val authorities: Collection<GrantedAuthority> = listOf(),
    val accessToken: String = "",
    val refreshToken: String? = ""
) {
    fun toUserEntity(): User {
        val name = attributes["display_name"] as String
        val picture = (attributes["images"] as ArrayList<LinkedHashMap<String, String>>?)?.get(0)?.get("url")
        return User(UUID.nameUUIDFromBytes(id.toByteArray()), name, picture)
    }
}