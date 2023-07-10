package com.denishovart.freqs.data

import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class ReactiveRedisComponent(private val redisOperations: ReactiveRedisOperations<String, Any>) {
    /**
     * Set key and value into a hash key
     * @param key key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @param val Object value
     * @return Mono of object
     */
    operator fun set(key: String, hashKey: String, `val`: Any): Mono<Any> {
        return redisOperations.opsForHash<Any, Any>().put(key, hashKey, `val`).map { b: Boolean? -> `val` }
    }

    /**
     * @param key key value - must not be null.
     * @return Flux of Object
     */
    operator fun get(key: String): Flux<Any> {
        return redisOperations.opsForHash<Any, Any>().values(key)
    }

    /**
     * Get value for given hashKey from hash at key.
     * @param key key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @return Object
     */
    operator fun get(key: String, hashKey: Any?): Mono<Any> {
        return redisOperations.opsForHash<Any, Any>()[key, hashKey!!]
    }

    /**
     * Delete a key that contained in a hash key.
     * @param key key value - must not be null.
     * @param hashKey hash key value -  must not be null.
     * @return 1 Success or 0 Error
     */
    fun remove(key: String, hashKey: Any?): Mono<Long> {
        return redisOperations.opsForHash<Any, Any>().remove(key, hashKey)
    }
}