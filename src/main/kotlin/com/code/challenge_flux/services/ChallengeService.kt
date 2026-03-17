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

    @Autowired
    private lateinit var userService: UserService

    /**
     * Создаёт задачу
     * @param username имя пользователя
     * @param challenge данные о задаче
     * @throws NoSuchElementException
     */
    suspend fun createChallenge(username: String, challenge: CodeChallengeDto) {
        val user = UserEntity.find { UsersTable.username eq username }.firstOrNull()
            ?: throw NoSuchElementException("пользователь с именем $username не найден")

        CodeChallengeEntity.new {
            name = challenge.name
            description = challenge.description
            challengeSource = challenge.challengeSource
            difficult = challenge.difficult
            solution = challenge.solution
            userEntity = user
        }
    }

    /**
     * Возвращает задачу
     * @param username имя пользователя
     * @param name название задачи
     */

    @Deprecated("Устаревший метод")
    suspend fun getChallenge(username: String, name: String): CodeChallengeDto {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        return CodeChallengeEntity.find {
            (CodeChallengesTable.userId eq userId) and (CodeChallengesTable.name eq name)
        }.first().toDto()
    }

    /**
     * Возвращает задачу, если не указан источник, то возращает первую найденую задачу
     * @param username имя пользователя
     * @param source источник задачи
     * @param name название задачи
     */
    suspend fun getChallenge(username: String, source: ChallengeSources? = null, name: String): CodeChallengeDto {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value

        return CodeChallengeEntity.find {
            (CodeChallengesTable.name eq name) and (CodeChallengesTable.userId eq userId) andIfNotNull (source?.let {
                CodeChallengesTable.challengeSource eq source
            })
        }.first().toDto()
    }


    /**
     * Возвращает список задач, если источник не указан, то возращает все задачи
     * @param username имя пользователя
     * @param source исчтоник задач
     */
    suspend fun getChallenges(username: String, source: ChallengeSources? = null): List<CodeChallengeDto> {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        return CodeChallengeEntity.find {
            (CodeChallengesTable.userId eq userId) andIfNotNull (source?.let {
                CodeChallengesTable.challengeSource eq source
            })
        }.map { it.toDto() }
    }

    /**
     * Обновляет задачу
     * @param username имя пользователя
     * @param updateData данные для обновления задачи
     */
    suspend fun updateChallenge(username: String, updateData: CodeChallengeDto): CodeChallengeDto {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        val challenge = CodeChallengeEntity.find {
            CodeChallengesTable.userId eq userId
        }.first()
        challenge.updateFromDto(updateData)
        return challenge.toDto()
    }

    /**
     * Удаляет задачу
     * @param username имя пользователя
     * @param name имя задачи
     */
    suspend fun deleteChallenge(username: String, name: String) {
        val userId = UserEntity.find { UsersTable.username eq username }.first().id.value
        CodeChallengeEntity.find {
            (CodeChallengesTable.userId eq userId) and (CodeChallengesTable.name eq name)
        }.first().delete()
    }

    /**
     * Загружает из выбраного источника
     * @param username имя пользователя
     * @param source источник
     */
    suspend fun addChallengeFromSource(username: String, source: ChallengeSources, offset: Int = 5) {
        val source = sourceManager.getSource(source)
        source.getChallenges(username, offset).forEach {
            createChallenge(username, it)
        }
    }


}

