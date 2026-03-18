package com.code.challenge_flux.controllers
import com.code.challenge_flux.CodeChallengeWebFluxApplication
import com.code.challenge_flux.data.challenge_sources.ChallengeSources
import com.code.challenge_flux.data.database.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.dto.UserDto
import com.code.challenge_flux.data.database.entities.CodeChallengeEntity
import com.code.challenge_flux.data.database.entities.UserEntity
import com.code.challenge_flux.data.database.tables.CodeChallengesTable
import com.code.challenge_flux.data.database.tables.UsersTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = [CodeChallengeWebFluxApplication::class]
)
class ChallengeControllerTest {

    val client = HttpClient(CIO)
    val address = "http://localhost:8080${Mapping.CHALLENGE}"
    val user1 = UserDto("email", "test", "123")
    val userId: UUID = UUID.randomUUID()
    val challengeId: UUID = UUID.randomUUID()

    val codeChallenge = CodeChallengeDto(
        "test",
        "test",
        ChallengeSources.CodeWars,
        "test",
        "test"
    )
    val codeChallenge1 = CodeChallengeDto(
        "tes1",
        "test",
        ChallengeSources.CodeWars,
        "test",
        "test"
    )
    val updateData = CodeChallengeDto(
        "test",
        "test1",
        ChallengeSources.CodeWars,
        "test",
        "test"
    )


    @BeforeEach
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

    @OptIn(InternalAPI::class)
    @Test
    fun createChallengeTest(): Unit = runBlocking {
        val request = client.post("$address/CodeWars/${user1.username}") {
            body = Json.encodeToString(codeChallenge1)
        }.bodyAsText()

        println(request)
        val challenge = transaction {
            CodeChallengeEntity.find {
                CodeChallengesTable.name eq codeChallenge.name
            }.firstOrNull()?.toDto()
        }
        assertEquals(codeChallenge, challenge)
    }

    @Test
    fun getCodeChallenge(): Unit = runBlocking {
        val request = client.get("$address/CodeWars/${user1.username}/${codeChallenge.name}"){
        }.bodyAsText()
        val challenge = Json.decodeFromString<CodeChallengeDto>(request)
        assertEquals(codeChallenge, challenge)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun updateCodeChallenge(): Unit = runBlocking {
        val request = client.put("$address/CodeWars/${user1.username}"){
            body = Json.encodeToString(updateData)
        }.bodyAsText()
        val challenge = Json.decodeFromString<CodeChallengeDto>(request)
        assertEquals(updateData, challenge)
    }


    @Test
    fun deleteCodeChallenge(): Unit = runBlocking {
        client.delete("$address/CodeWars/${user1.username}/${codeChallenge.name}")

        assertThrows<EntityNotFoundException> {
            transaction { CodeChallengeEntity[challengeId] }
        }
    }


    @AfterEach
    fun clear(): Unit = transaction {
        CodeChallengesTable.deleteAll()
        UsersTable.deleteAll()
    }

}