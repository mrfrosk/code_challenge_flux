package com.code.challenge_flux.data.database.dto.codewars

import com.code.challenge_flux.data.database.utils.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ShortChallengeDto(
    val id: String,
    val name: String,
    val slug: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val completedAt: LocalDateTime,
    val completedLanguages: List<String>
)