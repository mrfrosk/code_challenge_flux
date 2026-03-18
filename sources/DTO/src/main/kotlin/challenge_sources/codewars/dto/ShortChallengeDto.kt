package com.code.challenge_flux.data.database.challenge_sources.codewars.dto

import com.code.challenge_flux.services.utils.LocalDateTimeSerializer
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