package com.denishovart.freqs.config

import com.denishovart.freqs.auth.OAuth2AuthorizationRequestMixIn
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest


@Configuration
@Primary
class SerializationConfig {
    @Bean
    fun objectMapper(): ObjectMapper? {
        var objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val module = SimpleModule()
        module.addDeserializer(GrantedAuthority::class.java, GrantedAuthorityDeserializer())
        objectMapper.registerModule(module)
        objectMapper = ObjectMapper().addMixIn(
            OAuth2AuthorizationRequest::class.java, OAuth2AuthorizationRequestMixIn::class.java
        )
        return objectMapper
    }
}

class GrantedAuthorityDeserializer : JsonDeserializer<GrantedAuthority>() {

    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): GrantedAuthority {
        val authority = jsonParser.text
        return SimpleGrantedAuthority(authority)
    }
}