package com.denishovart.freqs.auth.dto

enum class AuthenticationStatus(
    val status: String
) {
    SUCCESS("Success"),
    FAILURE("Failure"),
    ACCESS_DENIED("Access denied");
}