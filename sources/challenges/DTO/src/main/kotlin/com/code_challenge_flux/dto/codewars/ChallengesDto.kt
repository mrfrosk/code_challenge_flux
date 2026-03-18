package com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars

import kotlinx.serialization.Serializable

@Serializable
data class ChallengesDto(val totalPages: Int, val totalItems: Int, val data: List<ShortChallengeDto>)