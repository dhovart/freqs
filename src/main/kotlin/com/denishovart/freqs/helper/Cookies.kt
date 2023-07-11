package com.denishovart.freqs.helper

import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.web.server.ServerWebExchange
import java.time.Duration
import java.util.*

const val AUTH_REQUEST_COOKIE = "oauth_request"
const val AUTH_COOKIE = "auth"

private fun buildResponseCookie(name: String, value: String?, duration: Duration, isSecure: Boolean) =
    ResponseCookie.from(name, value ?: "")
        .maxAge(duration)
        .httpOnly(true)
        .secure(isSecure)
        .path("/")
        .build()

fun ServerWebExchange.getAuthRequestCookie() =  this.request.cookies.getFirst(AUTH_REQUEST_COOKIE)

fun ServerWebExchange.setAuthRequestCookie(value: String, duration: Duration = Duration.ofDays(1)) =
    this.response.cookies.add(
        HttpHeaders.SET_COOKIE,
        buildResponseCookie(
            AUTH_REQUEST_COOKIE,
            value,
            Duration.ofDays(1),
            this.request.uri.scheme == "https"
        )
    )

fun ServerWebExchange.removeAuthRequestCookie() =
    this.response.cookies.add(HttpHeaders.SET_COOKIE, buildResponseCookie(
        AUTH_REQUEST_COOKIE,
        null,
        Duration.ofSeconds(0),
        this.request.uri.scheme == "https"
    ))

fun ServerWebExchange.getAuthId(password: String): String? {
    val cookie = this.request.cookies.getFirst(AUTH_COOKIE)
    return if (cookie?.value != null) EncryptionUtil.decrypt(cookie!!.value, password) else null
}

fun ServerWebExchange.setAuthCookie(value: String, password: String, duration: Duration = Duration.ofDays(999)) {
    val encryptedValue = EncryptionUtil.encrypt(value, password)
    return this.response.cookies.add(
        HttpHeaders.SET_COOKIE,
        buildResponseCookie(
            AUTH_COOKIE,
            encryptedValue,
            duration,
            this.request.uri.scheme == "https"
        ))
}

fun ServerWebExchange.removeAuthCookie() =
    this.response.cookies.add(
        HttpHeaders.SET_COOKIE,
        buildResponseCookie(
            AUTH_COOKIE,
            null,
            Duration.ofSeconds(0),
            this.request.uri.scheme == "https"
        ))
