package com.code.challenge_flux.services

import com.code.challenge_flux.data.challenge_sources.ChallengeSources
import com.code.challenge_flux.data.database.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.entities.CodeChallengeEntity
import com.code.challenge_flux.data.database.entities.UserEntity
import com.code.challenge_flux.data.database.tables.CodeChallengesTable
import com.code.challenge_flux.data.database.tables.UsersTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.andIfNotNull
import org.jetbrains.exposed.v1.core.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChallengeService {

    @Autowired
    private lateinit var sourceManager: SourceManager

    suspend fun createChallenge(username: String, challenge: CodeChallengeDto) {
        val user = UserEntity.find { UsersTable.username eq username }.first()
        CodeChallengeEntity.new {
            name = challenge.name
            description = challenge.description
            challengeSource = challenge.challengeSource
            difficult = challenge.difficult
            solution = challenge.solution
            userEntity = user
        }
    }

    suspend fun getChallenge(username: String, name: String): CodeChallengeDto {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        return CodeChallengeEntity.find {
            (CodeChallengesTable.userId eq userId) and (CodeChallengesTable.name eq name)
        }.first().toDto()
    }

    suspend fun getChallenge(username: String, source: ChallengeSources, name: String): CodeChallengeDto {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value

        return CodeChallengeEntity.find {
            ((CodeChallengesTable.userId eq userId)
                    and (CodeChallengesTable.name eq name)
                    and (CodeChallengesTable.challengeSource eq source))
        }.first().toDto()
    }


    suspend fun getChallenges(username: String, source: ChallengeSources? = null): List<CodeChallengeDto> {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        return CodeChallengeEntity.find {
            (CodeChallengesTable.userId eq userId) andIfNotNull (source?.let {
                CodeChallengesTable.challengeSource eq source
            })
        }.map { it.toDto() }
    }

    suspend fun updateChallenge(username: String, updateData: CodeChallengeDto): CodeChallengeDto {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        val challenge = CodeChallengeEntity.find {
            CodeChallengesTable.userId eq userId
        }.first()
        challenge.updateFromDto(updateData)
        return challenge.toDto()
    }

    suspend fun deleteChallenge(username: String, name: String) {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        CodeChallengeEntity.find {
            (CodeChallengesTable.userId eq userId) and (CodeChallengesTable.name eq name)
        }.first().delete()
    }

    suspend fun addChallengeFromSource(username: String, source: ChallengeSources, offset: Int = 5) {
        val source = sourceManager.getSource(source)
        source.getChallenges(username, offset).forEach {
            createChallenge(username, it)
        }
    }


}

