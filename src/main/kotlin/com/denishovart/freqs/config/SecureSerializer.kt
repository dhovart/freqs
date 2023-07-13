package com.denishovart.freqs.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

@Component
class SecureSerializer(
    @Qualifier("authObjectMapper")
    private val objectMapper: ObjectMapper,
    @Value("\${app.encryption.password}")
    private val password: String
) {
    companion object {
        private const val ENCRYPTION_ALGORITHM = "AES"
        private const val SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val KEY_LENGTH = 256
        private const val ITERATIONS = 10000
        private const val SALT_LENGTH = 16
    }

    fun <T : Any> encrypt(value: T): String {
        val salt = generateSalt()
        val secretKey = generateSecretKey(salt, password)

        val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val serializedObject = serializeObject(value)
        val encryptedBytes = cipher.doFinal(serializedObject)

        // Combine salt and encrypted data
        val combinedBytes = ByteArray(SALT_LENGTH + encryptedBytes.size)
        System.arraycopy(salt, 0, combinedBytes, 0, SALT_LENGTH)
        System.arraycopy(encryptedBytes, 0, combinedBytes, SALT_LENGTH, encryptedBytes.size)

        return Base64.getEncoder().encodeToString(combinedBytes)
    }

    fun <T : Any> decrypt(value: String, targetType: Class<T>): T {
        val combinedBytes = Base64.getDecoder().decode(value)
        val salt = ByteArray(SALT_LENGTH)
        val encryptedBytes = ByteArray(combinedBytes.size - SALT_LENGTH)

        System.arraycopy(combinedBytes, 0, salt, 0, SALT_LENGTH)
        System.arraycopy(combinedBytes, SALT_LENGTH, encryptedBytes, 0, encryptedBytes.size)

        val secretKey = generateSecretKey(salt, password)

        val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        val deserializedObject = deserializeObject<T>(decryptedBytes, targetType)
        return deserializedObject as T
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    private fun generateSecretKey(salt: ByteArray, password: String): SecretKey {
        val factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val tmp: SecretKey = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, ENCRYPTION_ALGORITHM)
    }

    private fun serializeObject(obj: Any): ByteArray {
        val serializedString = objectMapper.writeValueAsString(obj)
        return serializedString.toByteArray()
    }

    private fun <T> deserializeObject(bytes: ByteArray, targetType: Class<T>): T {
        return objectMapper.readValue(String(bytes), targetType)
    }
}
