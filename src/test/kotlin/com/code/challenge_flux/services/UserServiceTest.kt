package com.code.challenge_flux.services

import com.code.challenge_flux.data.database.dto.LoginDto
import com.code.challenge_flux.data.database.dto.UserDto
import com.code.challenge_flux.data.database.entities.UserEntity
import com.code.challenge_flux.data.database.tables.UsersTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest
class UserServiceTest {

    @Autowired
    lateinit var userService: UserService

    val user1 = UserDto("test@mail.ru", "testUsername", "123")
    val user2 = UserDto("test@mail.ru1", "testUsername1", "123")

    @BeforeEach
    fun init() {
        transaction {
            UsersTable.deleteAll()
            UserEntity.new {
                email = user1.email
                username = user1.username
                password = user1.password
            }
        }
    }

    @Test
    fun createUser() {
        runBlocking {
            val dbUser = suspendTransaction {
                userService.createUser(user2)
            }
            assertEquals(user2, dbUser)
        }
    }

    @Test
    fun getUser() {
        runBlocking {
            val user = suspendTransaction { userService.getUser(user1.username) }

            assertEquals(user1, user)
        }
    }

    @Test
    fun updateUser(){
        runBlocking {
            val updateData = UserDto("test3@mail.ru", "testUsername3", "123")
            suspendTransaction { userService.updateUser(user1.username, updateData) }

            val user =
                suspendTransaction {
                    UserEntity.find { UsersTable.email eq updateData.email }.first().toDto()
                }

            assertNotEquals(user1, user)
            assertEquals(updateData, user)
        }
    }

    @Test
    fun deleteUser(){
        val isNull = runBlocking {
            suspendTransaction {
                userService.deleteUser(user1.username)
                UserEntity.find { UsersTable.email eq user1.email }.firstOrNull()
            }
        }

        assertEquals(null, isNull)
    }

    @Test
    fun isExistsByMail(){
        runBlocking {
            val mustExists = suspendTransaction { userService.isExists(user1.email) }
            val mustNotExists = suspendTransaction { userService.isExists("") }
            assertEquals(true, mustExists)
            assertEquals(false, mustNotExists)
        }
    }

    @Test
    fun isExistsByDto(){
        runBlocking {
            val mustExists = suspendTransaction { userService.isExists(LoginDto(user1.email, user1.password)) }
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