package com.code_challenge_flux.api

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code_challenge_flux.core.fixtures.UsersFixture
import com.code_challenge_flux.core.services.database.entities.User
import com.code_challenge_flux.core.services.database.tables.UsersTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.web.reactive.config.WebFluxConfigurationSupport
import kotlin.test.Test

@SpringBootApplication(
    scanBasePackages = [
        "com.code_challenge_flux.core",
        "com.code_challenge_flux.dto",
        "com.code_challenge_flux.api",
        "com.code_challenge_flux.application",
    ],
    scanBasePackageClasses = [
        WebFluxConfigurationSupport::class,
    ],

)
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
)
class UserControllerTest: UsersFixture() {

    private val client = HttpClient(CIO)
    private val address = "http://localhost:8080${Mapping.USER}"

    @Test
    fun getUserTest() = runBlocking {
        val request = client.get("$address/${existedUser.username}")
        val user =
            Json.decodeFromString<com.code.challenge_flux.data.database.com.code_challenge_flux.dto.IdUserDto>(
                request.bodyAsText()
            )
        val userDto =
            UserDto(user.email, user.username, user.password)
        kotlin.test.assertEquals(existedUser, userDto)
    }

    @OptIn(io.ktor.utils.io.InternalAPI::class)
    @Test
    fun updateUser() = runBlocking {

        val request = client.put("$address/${existedUser.username}") {
            body =
                Json.encodeToString(updateData)
        }
        val user =
            Json.decodeFromString<UserDto>(
                request.bodyAsText()
            )
        kotlin.test.assertEquals(updateData, user)
    }

    @Test
    fun deleteUser(): Unit = runBlocking {
        client.delete("$address/${existedUser.username}")
        assertThrows<EntityNotFoundException> {
            transaction { User.Companion[userId] }
                }
    }

}