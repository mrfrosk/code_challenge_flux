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
    protected val userDto = UserDto("test", "test", "test")
    protected val codeChallenge = CodeChallengeDto(
        "test name",
        "test description",
        ChallengeSources.CodeWars,
        "8 kyu",
        "print('hello world')"
    )
    protected val codeChallenge1 = CodeChallengeDto(
        "test name1",
        "test description",
        ChallengeSources.CodeWars,
        "8 kyu",
        "print('hello world')"
    )

    @BeforeEach
    override fun setup() {
        tearDown()
        transaction {
            User.new(userId) {
                email = userDto.email
                username = userDto.username
                password = userDto.password
            }

            Challenge.new {
                name = codeChallenge.name
                description = codeChallenge.description
                challengeSource = codeChallenge.challengeSource
                difficult = codeChallenge.difficult
                solution = codeChallenge.solution
                user = User[userId]
            }
        }
    }
}