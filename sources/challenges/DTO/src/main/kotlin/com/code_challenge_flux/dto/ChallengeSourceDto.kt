package com.code.challenge_flux.data.database.com.code_challenge_flux.dto

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code.challenge_flux.data.database.com.code_challenge_flux.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChallengeSourceDto(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val challengeSource: ChallengeSources,
    val sourceUsername: String
)