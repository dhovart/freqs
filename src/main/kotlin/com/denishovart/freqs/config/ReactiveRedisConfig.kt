package com.denishovart.freqs.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class ReactiveRedisConfiguration(
    private val env: Environment,
    @Qualifier("authObjectMapper")
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(
            env.getProperty("spring.data.redis.host")!!,
            env.getProperty("spring.data.redis.port")!!.toInt()
        )
    }

    @Bean
    fun redisOperations(reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory?): ReactiveRedisOperations<String, Any> {
        val serializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)
        val builder = RedisSerializationContext.newSerializationContext<String, Any>(StringRedisSerializer())
        val context = builder.value(serializer).hashValue(serializer)
            .hashKey(serializer).build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory!!, context)
    }
}