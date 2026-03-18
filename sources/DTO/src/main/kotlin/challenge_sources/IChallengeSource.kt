package com.code.challenge_flux.data.database.challenge_sources

import com.code.challenge_flux.data.database.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.dto.UserDto

interface IChallengeSource {
    suspend fun getUser(username: String): UserDto
    suspend fun getChallenges(username: String, offset: Int): List<CodeChallengeDto>
    suspend fun getChallenge(id: String): CodeChallengeDto
}