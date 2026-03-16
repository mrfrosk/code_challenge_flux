package com.code.challenge_flux.data.database.dto

import com.code.challenge_flux.data.challenge_sources.ChallengeSources
import com.code.challenge_flux.services.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChallengeSourceDto(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val challengeSource: ChallengeSources,
    val sourceUsername: String
)