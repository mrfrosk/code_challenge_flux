package com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars

import kotlinx.serialization.Serializable
@Serializable
enum class ChallengeSources(name: String) {
    CodeWars("CodeWars"),

    LeetCode("LeetCode")
}