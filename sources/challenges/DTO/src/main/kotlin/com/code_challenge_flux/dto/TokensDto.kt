package com.code.challenge_flux.data.database.com.code_challenge_flux.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokensDto(
    @SerialName("access-token")
    val accessToken: String,
    @SerialName("refresh-token")
    val refreshToken: String
)
