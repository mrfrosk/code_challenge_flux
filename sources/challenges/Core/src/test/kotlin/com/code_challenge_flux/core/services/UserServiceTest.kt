package com.code_challenge_flux.core.services

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.LoginDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code_challenge_flux.core.services.database.entities.UserEntity
import com.code_challenge_flux.core.services.database.tables.UsersTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootApplication
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
)
class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    val userCreateDto1 = UserDto("test@mail.ru", "testUsername", "123")
    val user2CreateDto = UserDto("test@mail.ru1", "testUsername1", "123")

    @BeforeEach
    fun init() {
        transaction {
            UsersTable.deleteAll()
            UserEntity.Companion.new {
                email = userCreateDto1.email
                username = userCreateDto1.username
                password = userCreateDto1.password
            }
        }
    }

    @Test
    fun createUser() {
        runBlocking {
            val dbUser = suspendTransaction {
                userService.createUser(user2CreateDto)
            }
            val userDto = UserDto(dbUser.email, dbUser.username, dbUser.password)
            assertEquals(user2CreateDto, userDto)
        }
    }

    @Test
    fun getUser() {
        runBlocking {
            val user = suspendTransaction { userService.getUser(userCreateDto1.username) }
            val userDto = UserDto(user.email, user.username, user.password)
            assertEquals(userCreateDto1, userDto)
        }
    }

    /**
     * перевести тест с CreateUserDto на UserDto, если это нужно
     */
    @Test
    fun updateUser(){
        runBlocking {
            val updateData = UserDto("test3@mail.ru", "testUsername3", "123")
            suspendTransaction { userService.updateUser(userCreateDto1.username, updateData) }

            val user =
                suspendTransaction {
                    UserEntity.Companion.find { UsersTable.email eq updateData.email }.first().toDto()
                }
            val userDto = UserDto(user.email, user.username, user.password)
            assertNotEquals(userCreateDto1, userDto)
            assertEquals(updateData, userDto)
        }
    }

    @Test
    fun deleteUser(){
        val isNull = runBlocking {
            suspendTransaction {
                userService.deleteUser(userCreateDto1.username)
                UserEntity.Companion.find { UsersTable.email eq userCreateDto1.email }.firstOrNull()
            }
        }

        assertEquals(null, isNull)
    }

    @Test
    fun isExistsByMail(){
        runBlocking {
            val mustExists = suspendTransaction { userService.isExists(userCreateDto1.email) }
            val mustNotExists = suspendTransaction { userService.isExists("") }
            assertEquals(true, mustExists)
            assertEquals(false, mustNotExists)
        }
    }

    @Test
    fun isExistsByDto(){
        runBlocking {
            val mustExists =
                suspendTransaction { userService.isExists(LoginDto(userCreateDto1.email, userCreateDto1.password)) }
            val mustNotExists = suspendTransaction { userService.isExists(LoginDto("", "")) }
            assertEquals(true, mustExists)
            assertEquals(false, mustNotExists)
        }
    }


    @AfterEach
    fun clear() {
        transaction {
            UsersTable.deleteAll()
        }
    }


}