package com.code_challenge_flux.api

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code_challenge_flux.core.fixtures.ChallengeFixture
import com.code_challenge_flux.core.services.database.entities.Challenge
import com.code_challenge_flux.core.services.database.entities.Challenge.Companion
import com.code_challenge_flux.core.services.database.entities.User
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import kotlin.test.Test

@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
)
class ChallengeControllerTest: ChallengeFixture() {

    val client = HttpClient(CIO)
    val address = "http://localhost:8080${Mapping.CHALLENGE}"



    @OptIn(io.ktor.utils.io.InternalAPI::class)
    @Test
    fun createChallengeTest(): Unit = kotlinx.coroutines.runBlocking {

        val challenge = transaction {
            Challenge.find {
                CodeChallengesTable.name eq existedChallenge.name
            }.firstOrNull()?.toDto()
        }
        assertEquals(existedChallenge, challenge)
    }

    @Test
    fun getCodeChallenge(): Unit = kotlinx.coroutines.runBlocking {
        val path = "$address/CodeWars/${userData.username}/${existedChallenge.name}"
        val request = client.get(path)
        val challenge =
            Json.decodeFromString<CodeChallengeDto>(
                request.bodyAsText()
            )
        assertEquals(existedChallenge, challenge)
    }

    @OptIn(io.ktor.utils.io.InternalAPI::class)
    @Test
    fun updateCodeChallenge(): Unit = kotlinx.coroutines.runBlocking {
        val request = client.put("$address/CodeWars/${userData.username}") {
            body =
                Json.encodeToString(updateData)
        }.bodyAsText()
        val challenge =
            Json.decodeFromString<CodeChallengeDto>(
                request
            )
        assertEquals(updateData, challenge)
    }


    @Test
    fun deleteCodeChallenge(): Unit = kotlinx.coroutines.runBlocking {
        client.delete("$address/CodeWars/${userData.username}/${existedChallenge.name}")

       assertThrows<EntityNotFoundException> {
            transaction { Companion[challengeId] }
        }
    }




}