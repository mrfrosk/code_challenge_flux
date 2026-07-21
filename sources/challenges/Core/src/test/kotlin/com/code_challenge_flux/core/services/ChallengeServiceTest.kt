package com.code_challenge_flux.core.services

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code_challenge_flux.core.fixtures.ChallengeFixture
import com.code_challenge_flux.core.services.database.entities.Challenge
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@SpringBootTest
class ChallengeServiceTest : ChallengeFixture() {

    @Autowired
    lateinit var challengeService: ChallengeService


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


                val challengeFromDb = Challenge.find {
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
                Challenge.find {
                    CodeChallengesTable.name eq codeChallenge.name
                }.firstOrNull()
            }
            assertEquals(null, mustNull)
        }
    }

}