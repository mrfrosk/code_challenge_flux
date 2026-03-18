package com.code_challenge_flux.api

import com.code_challenge_flux.core.services.database.entities.UserEntity
import com.code_challenge_flux.core.services.database.tables.UsersTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.web.reactive.config.WebFluxConfigurationSupport

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
class UserControllerTest {

    val client = HttpClient(CIO)
    val address = "http://localhost:8080${Mapping.USER}"
    val user1 = com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto("email", "test", "123")
    val updateData = com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto("email", "test1", "124")
    val userId: java.util.UUID = java.util.UUID.randomUUID()


    @org.junit.jupiter.api.BeforeEach
    fun init(): Unit = org.jetbrains.exposed.v1.jdbc.transactions.transaction {
        UsersTable.deleteAll()
        UserEntity.new(userId) {
            email = user1.email
            username = user1.username
            password = user1.password
        }
    }

    @org.junit.jupiter.api.Test
    fun getUserTest() = kotlinx.coroutines.runBlocking {
        val request = client.get("$address/${user1.username}")
        val user =
            Json.decodeFromString<com.code.challenge_flux.data.database.com.code_challenge_flux.dto.IdUserDto>(
                request.bodyAsText()
            )
        val userDto =
            com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto(user.email, user.username, user.password)
        kotlin.test.assertEquals(user1, userDto)
    }

    @OptIn(io.ktor.utils.io.InternalAPI::class)
    @org.junit.jupiter.api.Test
    fun updateUser() = kotlinx.coroutines.runBlocking {

        val request = client.put("$address/${user1.username}") {
            body =
                Json.encodeToString(updateData)
        }
        val user =
            Json.Default.decodeFromString<com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto>(
                request.bodyAsText()
            )
        kotlin.test.assertEquals(updateData, user)
    }

    @org.junit.jupiter.api.Test
    fun deleteUser(): Unit = kotlinx.coroutines.runBlocking {
        client.delete("$address/${user1.username}")
        org.junit.jupiter.api.assertThrows<org.jetbrains.exposed.v1.dao.exceptions.EntityNotFoundException> {
                    org.jetbrains.exposed.v1.jdbc.transactions.transaction { UserEntity.Companion[userId] }
                }
    }
    

    @org.junit.jupiter.api.AfterEach
    fun clear(): Unit = org.jetbrains.exposed.v1.jdbc.transactions.transaction {
        UsersTable.deleteAll()
    }
}