package com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars

import kotlinx.serialization.Serializable

@Serializable
data class RanksDto(
    val overall: RankDto,
    val languages: Map<String, RankDto>
)
