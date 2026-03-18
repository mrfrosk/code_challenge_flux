package com.code_challenge_flux.core.services

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code_challenge_flux.core.services.database.entities.CodeChallengeEntity
import com.code_challenge_flux.core.services.database.entities.UserEntity
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import com.code_challenge_flux.core.services.database.tables.UsersTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest
class ChallengeServiceTest {

    @Autowired
    lateinit var challengeService: ChallengeService
    private final val userId = UUID.randomUUID()
    val userDto = UserDto("test", "test", "test")
    val codeChallenge = CodeChallengeDto(
        "test name",
        "test description",
        ChallengeSources.CodeWars,
        "8 kyu",
        "print('hello world')"
    )
    val codeChallenge1 = CodeChallengeDto(
        "test name1",
        "test description",
        ChallengeSources.CodeWars,
        "8 kyu",
        "print('hello world')"
    )

    @BeforeEach
    fun init() {
        transaction {
            UsersTable.deleteAll()
            CodeChallengesTable.deleteAll()
            UserEntity.Companion.new(userId) {
                email = userDto.email
                username = userDto.username
                password = userDto.password
            }

            CodeChallengeEntity.Companion.new {
                name = codeChallenge.name
                description = codeChallenge.description
                challengeSource = codeChallenge.challengeSource
                difficult = codeChallenge.difficult
                solution = codeChallenge.solution
                userEntity = UserEntity.Companion[userId]
            }
        }
    }

    @Test
    fun getCodeChallenge() {
        runBlocking {
            val codeChallengeBd = suspendTransaction {
                challengeService.getChallenge(userDto.username, codeChallenge.name)
            }
            assertEquals(codeChallenge, codeChallengeBd)
        }
    }

    @Test
    fun createChallenge() {
        runBlocking {
            val codeChallengeBd = suspendTransaction {
                challengeService.createChallenge(userDto.username, codeChallenge1)
                challengeService.getChallenge(userDto.username, codeChallenge1.name)
            }
            assertEquals(codeChallenge1, codeChallengeBd)
        }
    }

    @Test
    fun updateCodeChallenge() {
        runBlocking {
            suspendTransaction {
                val updateDifficult = CodeChallengeDto(
                    "test name1",
                    "test description",
                    ChallengeSources.CodeWars,
                    "3 kyu",
                    "print('hello world')"
                )

                val challengeDto = challengeService.updateChallenge(userDto.username, updateDifficult)


                val challengeFromDb = CodeChallengeEntity.Companion.find {
                    CodeChallengesTable.name eq codeChallenge1.name
                }.first().toDto()


                assertNotEquals(codeChallenge1, challengeDto)
                assertEquals(challengeDto, challengeFromDb)
            }
        }
    }

    @Test
    fun deleteCodeChallenge() {
        runBlocking {
            val mustNull = suspendTransaction {
                challengeService.deleteChallenge(userDto.username, codeChallenge.name)
                CodeChallengeEntity.Companion.find {
                    CodeChallengesTable.name eq codeChallenge.name
                }.firstOrNull()
            }
            assertEquals(null, mustNull)
        }
    }

    @AfterEach
    fun clear(): Unit = transaction {
        CodeChallengesTable.deleteAll()
        UsersTable.deleteAll()
    }

}