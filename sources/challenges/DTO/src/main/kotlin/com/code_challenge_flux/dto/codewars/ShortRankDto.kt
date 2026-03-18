package com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars

import kotlinx.serialization.Serializable

@Serializable
data class ShortRankDto(
    val id: Int,
    val name: String,
    val color: String
)