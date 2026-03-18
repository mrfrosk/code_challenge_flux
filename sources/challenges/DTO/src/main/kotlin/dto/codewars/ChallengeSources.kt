package com.code.challenge_flux.data.database.dto.codewars

import kotlinx.serialization.Serializable
enum class ChallengeSources(name: String) {
    @Serializable
    CodeWars("CodeWars"),
    @Serializable
    LeetCode("LeetCode")
}