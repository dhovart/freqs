package com.denishovart.freqs.config

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

@Configuration
@Primary
class SerializationConfig {
    @Bean
    fun objectMapper(): ObjectMapper? {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        val module = SimpleModule()
        module.addDeserializer(GrantedAuthority::class.java, GrantedAuthorityDeserializer())
        //objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(module)
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