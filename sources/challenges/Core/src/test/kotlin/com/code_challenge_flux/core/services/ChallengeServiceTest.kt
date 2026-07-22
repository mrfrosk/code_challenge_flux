package com.code_challenge_flux.core.services

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
                challengeService.getChallenge(userData.username, existedChallenge.name)
            }
            assertEquals(existedChallenge, codeChallengeBd)
        }
    }

    @Test
    fun createChallenge() {
        runBlocking {
            val codeChallengeBd = suspendTransaction {
                challengeService.createChallenge(userData.username, challengeBeforeUpdate)
                challengeService.getChallenge(userData.username, challengeBeforeUpdate.name)
            }
            assertEquals(challengeBeforeUpdate, codeChallengeBd)
        }
    }

    @Test
    fun updateCodeChallenge() {
        runBlocking {
            suspendTransaction {


                val challengeDto = challengeService.updateChallenge(userData.username, updateData)


                val challengeFromDb = Challenge.find {
                    CodeChallengesTable.name eq updateData.name
                }.first().toDto()


                assertNotEquals(challengeBeforeUpdate, challengeDto)
                assertEquals(challengeDto, challengeFromDb)
            }
        }
    }

    @Test
    fun deleteCodeChallenge() {
        runBlocking {
            val mustNull = suspendTransaction {
                challengeService.deleteChallenge(userData.username, existedChallenge.name)
                Challenge.find {
                    CodeChallengesTable.name eq existedChallenge.name
                }.firstOrNull()
            }
            assertEquals(null, mustNull)
        }
    }

}