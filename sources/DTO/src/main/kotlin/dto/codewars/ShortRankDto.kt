package com.code.challenge_flux.data.database.dto.codewars

import kotlinx.serialization.Serializable

@Serializable
data class ShortRankDto(
    val id: Int,
    val name: String,
    val color: String
)