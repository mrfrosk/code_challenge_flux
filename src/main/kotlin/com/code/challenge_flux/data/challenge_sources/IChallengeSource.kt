package com.code.challenge_flux.data.challenge_sources

import com.code.challenge_flux.data.database.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.dto.CreateUserDto

interface IChallengeSource {
    suspend fun getUser(username: String): CreateUserDto
    suspend fun getChallenges(username: String, offset: Int): List<CodeChallengeDto>
    suspend fun getChallenge(id: String): CodeChallengeDto
}