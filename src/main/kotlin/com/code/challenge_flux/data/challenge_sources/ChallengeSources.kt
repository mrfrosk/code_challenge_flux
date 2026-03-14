package com.code.challenge_flux.data.challenge_sources

import kotlinx.serialization.Serializable


enum class ChallengeSources(name: String) {
    @Serializable
    CodeWars("CodeWars"),
    @Serializable
    LeetCode("LeetCode")
}