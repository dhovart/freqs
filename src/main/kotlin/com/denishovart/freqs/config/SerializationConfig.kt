package com.denishovart.freqs.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.jackson2.CoreJackson2Module
import org.springframework.security.jackson2.SimpleGrantedAuthorityMixin
import org.springframework.security.oauth2.client.jackson2.OAuth2ClientJackson2Module


@Configuration
@Primary
class SerializationConfig {

    @Primary
    @Bean
    fun defaultObjectMapper(): ObjectMapper {
        var objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        return objectMapper
    }

    @Bean
    fun authObjectMapper(): ObjectMapper {
        var objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        // when registering the 2 following modules for the default mapper,
        // it messes up the graphql mapper settings
        objectMapper.registerModule(CoreJackson2Module())
        objectMapper.registerModule(OAuth2ClientJackson2Module())

        return objectMapper
    }
}