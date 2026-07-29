package com.code_challenge_flux.core.services.challenge_sources.codewars

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengesDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ShortChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.UserSourceDto
import com.code_challenge_flux.core.services.database.entities.CodeChallengeEntity
import com.code_challenge_flux.core.services.database.entities.UserEntity
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import com.code_challenge_flux.core.services.database.tables.UsersTable
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.springframework.stereotype.Service
import com.code_challenge_flux.core.services.challenge_sources.IChallengeSource
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID
import kotlin.math.ceil


@Service
class CodeWarsSource : IChallengeSource {
    private val userUrl = "https://www.codewars.com/api/v1/users"
    private val challengeUrl = "code-challenges/completed"
    private val challengeInfoUrl = "https://www.codewars.com/api/v1/code-challenges/"
    private val client = HttpClient(CIO)

    override suspend fun getUser(username: String): UserDto {
        TODO("пока не решил, должен ли существовать этот метод в принципе")
    }

    private suspend fun getChallengesInfo(username: String): List<ShortChallengeDto> {
        val request = client.get("$userUrl/$username/$challengeUrl").bodyAsText()
        return Json.decodeFromString<ChallengesDto>(request).data

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadChallenges(username: String): Flow<CodeChallengeDto> = flow {
        val totalCompiled = client.get("$userUrl/$username")
            .body<UserSourceDto>()
            .codeChallenges.totalCompleted

        /**
         * 200F и 10 в concurrency заменить на значения из конфига
         */
        val pages = ceil(totalCompiled / 200F).toInt()
        val challenges = (0 until pages).asFlow().flatMapMerge(concurrency = 10) { pageIdx ->
            runCatching {
                client.get("$userUrl/$username/$challengeUrl") {
                    parameter("page", pageIdx)
                }
            }.onFailure { exception ->
                if (exception is CancellationException) throw exception
                /**
                 * Заменить на нормальное логирование
                 */
                println(exception.message)
            }.getOrNull()?.let { response ->
                if (response.status.isSuccess()) response.body<ChallengesDto>().data.asFlow() else emptyFlow()
            } ?: emptyFlow()


        }.flatMapMerge(concurrency = 10) {
            flow {
                runCatching {
                    client.get("$challengeInfoUrl/${it.id}")
                }.onFailure { exception ->
                    if (exception is CancellationException) throw exception
                    /**
                     * Заменить на нормальное логирование
                     */
                    println(exception.message)
                }.getOrNull()?.let { response ->
                    if (response.status.isSuccess()) {
                        val challenge = response.body<ChallengeDto>()
                        emit(toCodeChallenge(challenge))
                    }
                }
            }
        }
        emitAll(challenges)
    }.flowOn(Dispatchers.IO)

    private suspend inline fun <reified T> getResponseOrEmpty(response: HttpResponse): T?{
        return try {
            response.body<T>()
        }catch (e: CancellationException){
            throw e
        }catch (_: Throwable){
            null
        }
    }

    override suspend fun getChallenge(id: String): CodeChallengeDto {
        val request = client.get(challengeInfoUrl + id).bodyAsText()
        val body = Json.decodeFromString<ChallengeDto>(request)
        return toCodeChallenge(body)
    }

    override suspend fun getChallenges(username: String, offset: Int): List<CodeChallengeDto> {
        val user = UserEntity.find { UsersTable.username eq username }.first()
        val challenges = getChallengesInfo(username)
        val lastChallenge = getLastChallenge(user.id.value)
        val lastSavedIndex = if (lastChallenge == null) {
            0
        } else {
            val challenge = challenges.find { it.name == lastChallenge.name }
            challenges.indexOf(challenge)
        }
        return if (challenges.size - lastSavedIndex <= offset) {
            challenges.subList(lastSavedIndex, challenges.size).map {
                getChallenge(it.id)
            }
        } else {
            challenges.subList(lastSavedIndex, lastSavedIndex + offset).map {
                getChallenge(it.id)
            }
        }
    }

    private fun getLastChallenge(userId: UUID): CodeChallengeDto? {
        val challenge = CodeChallengeEntity.find { CodeChallengesTable.userId eq userId }.lastOrNull()
        return challenge?.toDto()
    }

    private fun toCodeChallenge(challenge: ChallengeDto) = CodeChallengeDto(
        challenge.name,
        challenge.description,
        ChallengeSources.CodeWars,
        challenge.rank.name,
        ""
    )
}