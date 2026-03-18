package com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars

import kotlinx.serialization.Serializable
enum class ChallengeSources(name: String) {
    @Serializable
    CodeWars("CodeWars"),
    @Serializable
    LeetCode("LeetCode")
}