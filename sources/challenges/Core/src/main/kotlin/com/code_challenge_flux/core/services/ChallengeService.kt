package com.code_challenge_flux.core.services

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code_challenge_flux.core.services.database.entities.CodeChallengeEntity
import com.code_challenge_flux.core.services.database.entities.UserEntity
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.uuid.Uuid

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
    suspend fun createChallenge(username: String, challenge: CodeChallengeDto): CodeChallengeDto {
        val userId = userService.getUser(username).id

        return CodeChallengeEntity.new {
            name = challenge.name
            description = challenge.description
            challengeSource = challenge.challengeSource
            difficult = challenge.difficult
            solution = challenge.solution
            user = UserEntity[userId]
        }.toDto()
    }

    /**
     * Возвращает задачу
     * @param username имя пользователя
     * @param name название задачи
     */

    @Deprecated("Устаревший метод")
    suspend fun getChallenge(username: String, name: String): CodeChallengeDto {
        val userId = userService.getUser(username).id
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
        val userId = userService.getUser(username).id

        return CodeChallengeEntity.find {
            (CodeChallengesTable.name eq name) and (CodeChallengesTable.userId eq userId) andIfNotNull (source?.let {
                CodeChallengesTable.challengeSource eq source
            })
        }.first().toDto()
    }


    /**
     * Возвращает поток задач, если источник не указан, то возвращает все задачи
     * @param username имя пользователя
     * @param source источник задач
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getChallenges(username: String, source: ChallengeSources? = null) = channelFlow {
        suspendTransaction {
            val userId = userService.getUser(username).id

            val query = CodeChallengesTable.selectAll().where {
                (CodeChallengesTable.userId eq userId) andIfNotNull source?.let {
                    CodeChallengesTable.challengeSource eq source
                }
            }
            query.fetchSize(100)
            CodeChallengeEntity.wrapRows(query).forEach {
                send(it.toDto())
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Возвращает поток задач, если источник не указан, то возвращает все задачи
     * @param username имя пользователя
     * @param source источник задач
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getChallengesM2(username: String, source: ChallengeSources? = null): Flow<CodeChallengeDto> = flow {
        val userId = suspendTransaction { userService.getUser(username).id }
        var hasNext = true
        var lastId: Uuid? = null
        while (hasNext) {
            val startId = lastId
            val challengesAndLastId = suspendTransaction {
                val query = CodeChallengesTable.selectAll().where {
                    (CodeChallengesTable.userId eq userId) andIfNotNull (source?.let {
                        CodeChallengesTable.challengeSource eq source
                    }) andIfNotNull (startId?.let {
                        CodeChallengesTable.id less startId
                    })
                }.limit(50).orderBy(CodeChallengesTable.id to SortOrder.DESC)

                val challenges = CodeChallengeEntity.wrapRows(query)

                Pair(challenges.last().id.value, challenges.map { it.toDto() })
            }

            hasNext = !challengesAndLastId.second.isEmpty()
            lastId = challengesAndLastId.first

            challengesAndLastId.second.forEach {
                emit(it)
            }
        }
    }


    /**
     * Обновляет задачу
     * @param username имя пользователя
     * @param updateData данные для обновления задачи
     */
    suspend fun updateChallenge(username: String, updateData: CodeChallengeDto): CodeChallengeDto {
        val userId = userService.getUser(username).id
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
        val userId = userService.getUser(username).id
        CodeChallengeEntity.find {
            (CodeChallengesTable.userId eq userId) and (CodeChallengesTable.name eq name)
        }.first().delete()
    }

    /**
     * Загружает задачи из источника
     * @param username имя пользователя
     * @param source источник
     */
    suspend fun loadFromSource(username: String, source: ChallengeSources) {
        val source = sourceManager.getSource(source)
        coroutineScope {
            source.getChallenges(username).collect {
                launch(Dispatchers.IO) {
                    createChallenge(username, it)
                }
            }
        }
    }
}





