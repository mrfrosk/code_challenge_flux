package com.code.challenge_flux.data.database.dto.codewars

import kotlinx.serialization.Serializable

@Serializable
data class RankDto(
    val rank: Int,
    val name: String,
    val color: String,
    val score: Int
)
