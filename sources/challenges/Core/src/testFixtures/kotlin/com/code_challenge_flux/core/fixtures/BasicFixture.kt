package com.code_challenge_flux.core.fixtures

import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import com.code_challenge_flux.core.services.database.tables.UsersTable
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.AfterEach

abstract class BasicFixture {

    protected abstract fun setup()

    @AfterEach
    protected open fun tearDown() {
        transaction {
            CodeChallengesTable.deleteAll()
            UsersTable.deleteAll()
        }
    }
}