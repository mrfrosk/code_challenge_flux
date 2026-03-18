package com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars

import kotlinx.serialization.Serializable

@Serializable
data class UserSourceDto(
    val id: String,
    val username: String,
    val name: String?,
    val honor: Int,
    val clan: String?,
    val leaderboardPosition: Int?,
    val skills: List<String>?,
    val ranks: RanksDto,
    val codeChallenges: Map<String, Int>
)