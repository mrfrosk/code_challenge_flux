package com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars

import kotlinx.serialization.Serializable

@Serializable
data class RankDto(
    val rank: Int,
    val name: String,
    val color: String,
    val score: Int
)
