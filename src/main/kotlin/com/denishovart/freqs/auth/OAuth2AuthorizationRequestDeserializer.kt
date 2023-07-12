package com.denishovart.freqs.auth

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.MissingNode
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import java.io.IOException

internal class OAuth2AuthorizationRequestDeserializer :
    JsonDeserializer<OAuth2AuthorizationRequest?>() {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, dctx: DeserializationContext): OAuth2AuthorizationRequest {
        val mapper = jp.codec as ObjectMapper
        val jsonNode = mapper.readTree<JsonNode>(jp)
        val builder= OAuth2AuthorizationRequest.authorizationCode()
        val scopes = mapper.convertValue(
            jsonNode["scopes"], object: TypeReference<Set<String>>() {}
        )
        val additionalParameters = mapper.convertValue(
            jsonNode["additionalParameters"], object: TypeReference<Map<String, Any>>() {}
        )
        val attributes = mapper.convertValue(
            jsonNode["attributes"], object: TypeReference<Map<String, Any>>() {}
        )
        return builder
            .clientId(readJsonNode(jsonNode, "clientId").asText())
            .authorizationUri(readJsonNode(jsonNode, "authorizationUri").asText())
            .redirectUri(readJsonNode(jsonNode, "redirectUri").asText())
            .scopes(scopes)
            .state(readJsonNode(jsonNode, "state").asText())
            .additionalParameters(additionalParameters)
            .attributes(attributes)
            .build()
    }

    private fun readJsonNode(jsonNode: JsonNode, field: String): JsonNode {
        return if (jsonNode.has(field)) jsonNode[field] else MissingNode.getInstance()
    }
}