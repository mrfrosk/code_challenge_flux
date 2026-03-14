package com.code.challenge_flux.data.challenge_sources.codewars.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChallengesDto(val totalPages: Int, val totalItems: Int, val data: List<ShortChallengeDto>)