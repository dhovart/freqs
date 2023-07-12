package com.denishovart.freqs.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.io.IOException

class GrantedAuthorityDeserializer : JsonDeserializer<SimpleGrantedAuthority?>() {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): SimpleGrantedAuthority {
        val mapper = jp.codec as ObjectMapper
        val jsonNode = mapper.readTree<JsonNode>(jp)
        return SimpleGrantedAuthority(jsonNode["authority"].asText())
    }
}