package com.code.challenge_flux.data.database.com.code_challenge_flux.dto

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import kotlinx.serialization.Serializable

@Serializable
data class CodeChallengeDto(
    val name: String,
    val description: String,
    val challengeSource: ChallengeSources,
    val difficult: String,
    val solution: String,
)