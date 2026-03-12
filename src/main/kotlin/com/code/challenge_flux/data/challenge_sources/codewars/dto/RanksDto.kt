package com.code.challenge_flux.data.challenge_sources.codewars.dto

import com.code.challenge_flux.data.challenge_sources.codewars.dto.RankDto
import kotlinx.serialization.Serializable

@Serializable
data class RanksDto(
    val overall: RankDto,
    val languages: Map<String, RankDto>
)
