package com.code_challenge_flux.core.services

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.LoginDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code_challenge_flux.core.fixtures.UsersFixture
import com.code_challenge_flux.core.services.database.entities.User
import com.code_challenge_flux.core.services.database.tables.UsersTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
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
class UserServiceTest : UsersFixture() {

    @Autowired
    lateinit var userService: UserService

    @Test
    fun create() {
        runBlocking {
            val userFromDb = suspendTransaction {
                userService.createUser(createUser)
            }
            val exceptedUser = UserDto(userFromDb.email, userFromDb.username, userFromDb.password)
            assertEquals(createUser, exceptedUser)
        }
    }

    @Test
    fun get() {
        runBlocking {
            val user = suspendTransaction { userService.getUser(existedUser.username) }
            val userDto = UserDto(user.email, user.username, user.password)
            assertEquals(existedUser, userDto)
        }
    }

    /**
     * Перенести тест с CreateUserDto на UserDto, если это нужно
     */
    @Test
    fun update() {
        runBlocking {

            suspendTransaction { userService.updateUser(existedUser.username, updateData) }

            val user =
                suspendTransaction {
                    User.find { UsersTable.email eq updateData.email }.first().toDto()
                }
            val userDto = UserDto(user.email, user.username, user.password)
            assertNotEquals(existedUser, userDto)
            assertEquals(updateData, userDto)
        }
    }

    @Test
    fun delete() {
        val isNull = runBlocking {
            suspendTransaction {
                userService.deleteUser(existedUser.username)
                User.find { UsersTable.email eq existedUser.email }.firstOrNull()
            }
        }

        assertEquals(null, isNull)
    }

    @Test
    fun existsByEmail() {
        runBlocking {
            val mustExists = suspendTransaction { userService.isExists(existedUser.email) }
            val mustNotExists = suspendTransaction { userService.isExists("") }
            assertEquals(true, mustExists)
            assertEquals(false, mustNotExists)
        }
    }

    @Test
    fun existsByDto() {
        runBlocking {
            val mustExists =
                suspendTransaction { userService.isExists(LoginDto(existedUser.email, existedUser.password)) }
            val mustNotExists = suspendTransaction { userService.isExists(LoginDto("", "")) }
            assertEquals(true, mustExists)
            assertEquals(false, mustNotExists)
        }
    }

}