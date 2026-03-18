package com.code.challenge_flux.controllers

import com.code.challenge_flux.CodeChallengeWebFluxApplication
import com.code.challenge_flux.data.database.dto.UserDto
import com.code.challenge_flux.data.database.dto.IdUserDto
import services.database.entities.UserEntity
import services.database.tables.UsersTable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = [CodeChallengeWebFluxApplication::class]
)
class UserControllerTest {

    val client = HttpClient(CIO)
    val address = "http://localhost:8080${Mapping.USER}"
    val user1 = UserDto("email", "test", "123")
    val updateData = UserDto("email", "test1", "124")
    val userId: UUID = UUID.randomUUID()


    @BeforeEach
    fun init(): Unit = transaction {
        UsersTable.deleteAll()
        UserEntity.new(userId) {
            email = user1.email
            username = user1.username
            password = user1.password
        }
    }

    @Test
    fun getUserTest() = runBlocking {
        val request = client.get("$address/${user1.username}")
        val user = Json.decodeFromString<IdUserDto>(request.bodyAsText())
        val userDto = UserDto(user.email, user.username, user.password)
        assertEquals(user1, userDto)
    }

    @OptIn(InternalAPI::class)
    @Test
    fun updateUser() = runBlocking {
        
        val request = client.put("$address/${user1.username}") {
            body = Json.encodeToString(updateData)
        }
        val user = Json.decodeFromString<UserDto>(request.bodyAsText())
        assertEquals(updateData, user)
    }

    @Test
    fun deleteUser(): Unit = runBlocking {
        client.delete("$address/${user1.username}")
        assertThrows<EntityNotFoundException> {
            transaction { UserEntity[userId] }
        }
    }
    

    @AfterEach
    fun clear(): Unit = transaction {
        UsersTable.deleteAll()
    }
}