package com.code_challenge_flux.core.fixtures

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import com.code_challenge_flux.core.services.database.entities.Challenge
import com.code_challenge_flux.core.services.database.entities.User
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import java.util.*

open class ChallengeFixture : BasicFixture() {
    protected val userId: UUID = UUID.randomUUID()
    protected val userData = UserDto("email", "test", "test")
    protected val existedChallenge = CodeChallengeDto(
        "test_name",
        "test description",
        ChallengeSources.CodeWars,
        "8 kyu",
        "print('hello world')"
    )
    protected val challengeBeforeUpdate = CodeChallengeDto(
        "test name1",
        "test description",
        ChallengeSources.CodeWars,
        "8 kyu",
        "print('hello world')"
    )

    protected val challengeId: UUID = UUID.randomUUID()


    protected val updateData = CodeChallengeDto(
        "test", "test1", ChallengeSources.CodeWars, "test", "test"
    )

    @BeforeEach
    override fun setup() {
        tearDown()
        transaction {
            User.new(userId) {
                email = userData.email
                username = userData.username
                password = userData.password
            }

            Challenge.new(challengeId) {
                name = existedChallenge.name
                description = existedChallenge.description
                challengeSource = existedChallenge.challengeSource
                difficult = existedChallenge.difficult
                solution = existedChallenge.solution
                user = User[userId]
            }
        }
    }
}