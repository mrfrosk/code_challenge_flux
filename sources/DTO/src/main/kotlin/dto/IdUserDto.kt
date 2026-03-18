package com.code.challenge_flux.data.database.dto

import com.code.challenge_flux.data.database.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class IdUserDto(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val email: String,
    val username: String,
    val password: String
)
