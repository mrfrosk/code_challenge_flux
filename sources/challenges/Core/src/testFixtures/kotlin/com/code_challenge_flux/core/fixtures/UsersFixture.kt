package com.code_challenge_flux.core.fixtures

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code_challenge_flux.core.services.database.entities.User
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import java.util.UUID


abstract class UsersFixture : BasicFixture() {

    protected val existedUser = UserDto("test@mail.ru", "testUsername", "123")
    protected val createUser = UserDto("test@mail.ru1", "testUsername1", "123")
    protected val updateData = UserDto("test3@mail.ru", "testUsername3", "123")
    protected val userId: UUID = UUID.randomUUID()
    @BeforeEach
    override fun setup() {
        tearDown()
        transaction {
            User.new {
                email = existedUser.email
                username = existedUser.username
                password = existedUser.password
            }
        }
    }


}