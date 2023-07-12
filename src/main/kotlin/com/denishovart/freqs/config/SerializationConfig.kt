package com.denishovart.freqs.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest


@Configuration
@Primary
class SerializationConfig {
    @Bean
    fun objectMapper(): ObjectMapper? {
        var objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val oAuth2AuthorizationRequestModule = SimpleModule()
        oAuth2AuthorizationRequestModule.addDeserializer(OAuth2AuthorizationRequest::class.java, OAuth2AuthorizationRequestDeserializer())
        objectMapper.registerModule(oAuth2AuthorizationRequestModule)
        val grantedAuthorityModule = SimpleModule()
        grantedAuthorityModule.addDeserializer(GrantedAuthority::class.java, GrantedAuthorityDeserializer())
        objectMapper.registerModule(grantedAuthorityModule)
        return objectMapper
    }
}