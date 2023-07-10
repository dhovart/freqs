package com.denishovart.freqs.helper

import org.springframework.util.SerializationUtils
import java.util.Base64

object Base64 {
    private val B64E: Base64.Encoder = Base64.getEncoder()
    private val B64D: Base64.Decoder = Base64.getDecoder()

    fun <T> encrypt(obj: T): String {
        val bytes: ByteArray? = SerializationUtils.serialize(obj)
        return B64E.encodeToString(bytes)
    }

    fun <T> decrypt(encrypted: String): T {
        val encryptedBytes: ByteArray = B64D.decode(encrypted)
        return SerializationUtils.deserialize(encryptedBytes) as T
    }
}