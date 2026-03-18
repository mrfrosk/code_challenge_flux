package com.code_challenge_flux.api

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code_challenge_flux.core.services.database.entities.CodeChallengeEntity
import com.code_challenge_flux.core.services.database.entities.UserEntity
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import com.code_challenge_flux.core.services.database.tables.UsersTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import java.util.*

@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
)
class ChallengeControllerTest {

    val client = HttpClient(CIO)
    val address = "http://localhost:8080${Mapping.CHALLENGE}"
    val user1 = UserDto("email", "test", "123")
    val userId: UUID = UUID.randomUUID()
    val challengeId: UUID = UUID.randomUUID()

    val codeChallenge = CodeChallengeDto(
        "test", "test", ChallengeSources.CodeWars, "test", "test"
    )
    val codeChallenge1 = CodeChallengeDto(
        "tes1", "test", ChallengeSources.CodeWars, "test", "test"
    )
    val updateData = CodeChallengeDto(
        "test", "test1", ChallengeSources.CodeWars, "test", "test"
    )


    @org.junit.jupiter.api.BeforeEach
    fun init(): Unit = transaction {
        CodeChallengesTable.deleteAll()
        UsersTable.deleteAll()
        UserEntity.new(userId) {
            email = user1.email
            username = user1.username
            password = user1.password
        }
        CodeChallengeEntity.new(challengeId) {
            name = codeChallenge.name
            description = codeChallenge.description
            challengeSource = codeChallenge.challengeSource
            difficult = codeChallenge.difficult
            solution = codeChallenge.solution
            userEntity = UserEntity[userId]
        }
    }

    @OptIn(io.ktor.utils.io.InternalAPI::class)
    @kotlin.test.Test
    fun createChallengeTest(): Unit = kotlinx.coroutines.runBlocking {
        val request = client.post("$address/CodeWars/${user1.username}") {
            body =
                Json.encodeToString(codeChallenge1)
        }.bodyAsText()

        println(request)
        val challenge = transaction {
            CodeChallengeEntity.find {
                CodeChallengesTable.name eq codeChallenge.name
            }.firstOrNull()?.toDto()
        }
        kotlin.test.assertEquals(codeChallenge, challenge)
    }

    @kotlin.test.Test
    fun getCodeChallenge(): Unit = kotlinx.coroutines.runBlocking {
        val request = client.get("$address/CodeWars/${user1.username}/${codeChallenge.name}") {}.bodyAsText()
        val challenge =
            Json.Default.decodeFromString<CodeChallengeDto>(
                request
            )
        kotlin.test.assertEquals(codeChallenge, challenge)
    }

    @OptIn(io.ktor.utils.io.InternalAPI::class)
    @kotlin.test.Test
    fun updateCodeChallenge(): Unit = kotlinx.coroutines.runBlocking {
        val request = client.put("$address/CodeWars/${user1.username}") {
            body =
                Json.encodeToString(updateData)
        }.bodyAsText()
        val challenge =
            Json.decodeFromString<CodeChallengeDto>(
                request
            )
        kotlin.test.assertEquals(updateData, challenge)
    }


    @kotlin.test.Test
    fun deleteCodeChallenge(): Unit = kotlinx.coroutines.runBlocking {
        client.delete("$address/CodeWars/${user1.username}/${codeChallenge.name}")

        org.junit.jupiter.api.assertThrows<org.jetbrains.exposed.v1.dao.exceptions.EntityNotFoundException> {
            transaction { CodeChallengeEntity.Companion[challengeId] }
        }
    }


    @org.junit.jupiter.api.AfterEach
    fun clear(): Unit = transaction {
        CodeChallengesTable.deleteAll()
        UsersTable.deleteAll()
    }

}