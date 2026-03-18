package com.code_challenge_flux.core.services.challenge_sources

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto

interface IChallengeSource {
    suspend fun getUser(username: String): UserDto
    suspend fun getChallenges(username: String, offset: Int): List<CodeChallengeDto>
    suspend fun getChallenge(id: String): CodeChallengeDto
}