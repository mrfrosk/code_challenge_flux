package com.code_challenge_flux.core.services.challenge_sources.codewars

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengesDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ShortChallengeDto
import com.code_challenge_flux.core.services.challenge_sources.IChallengeSource
import com.code_challenge_flux.core.services.database.entities.Challenge
import com.code_challenge_flux.core.services.database.entities.User
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import com.code_challenge_flux.core.services.database.tables.UsersTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.springframework.stereotype.Service
import java.util.*


@Service
class CodeWarsSource : IChallengeSource {
    private val userUrl = "https://www.codewars.com/api/v1/users"
    private val challengeUrl = "code-challenges/completed"
    private val challengeInfoUrl = "https://www.codewars.com/api/v1/code-challenges/"
    private val client = HttpClient(CIO){
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = INFINITE_TIMEOUT_MS
            socketTimeoutMillis = 5_000
            connectTimeoutMillis = 20_000
        }
    }

    override suspend fun getUser(username: String): UserDto {
        TODO("пока не решил, должен ли существовать этот метод в принципе")
    }

    private suspend fun getChallengesInfo(username: String): List<ShortChallengeDto> {
        val request = client.get("$userUrl/$username/$challengeUrl").bodyAsText()
        return Json.decodeFromString<ChallengesDto>(request).data

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getChallenges(username: String): Flow<CodeChallengeDto> = flow {
        val request = client.get("$userUrl/$username")
        val totalCompiled = request.body<UserSourceDto>()
            .codeChallenges.totalCompleted

        /**
         * 200F и 10 в concurrency заменить на значения из конфига
         */
        val pages = ceil(totalCompiled / 200F).toInt()
        val challenges = (0 until pages).asFlow().flatMapMerge(concurrency = 10) { pageIdx ->
            val pages = getResponseOrEmpty<ChallengesDto> {
                client.get("$userUrl/$username/$challengeUrl") {
                    parameter("page", pageIdx)
                }
            }?.data?.asFlow() ?: emptyFlow()
            pages.flatMapMerge(concurrency = 10) {
                flow {
                    getResponseOrEmpty<ChallengeDto> {
                        client.get("$challengeInfoUrl/${it.id}")
                    }?.let { challenge ->
                        emit(toCodeChallenge(challenge))
                    }

                }
            }
        }
        emitAll(challenges)
    }.flowOn(Dispatchers.IO)

    private suspend inline fun <reified T> getResponseOrEmpty(block: () -> HttpResponse): T? {
        val response = try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println(e.message)
            println("добавить правильное логирование")
            return null
        }
        response.status.isSuccess().let { isSuccess ->
            if (!isSuccess) {
                println("Не Success")
                return null
            }
        }
        return try {
            response.body<T>()
        } catch (e: Exception) {
            println(e.message)
            println("добавить правильное логирование, ${e.javaClass.name}")
            null
        }
    }

    override suspend fun getChallenge(id: String): CodeChallengeDto {
        val request = client.get(challengeInfoUrl + id).bodyAsText()
        val body = Json.decodeFromString<ChallengeDto>(request)
        return toCodeChallenge(body)
    }

    override suspend fun getChallenges(username: String, offset: Int): List<CodeChallengeDto> {
        val user = User.find { UsersTable.username eq username }.first()
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
        val challenge = Challenge.find { CodeChallengesTable.userId eq userId }.lastOrNull()
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