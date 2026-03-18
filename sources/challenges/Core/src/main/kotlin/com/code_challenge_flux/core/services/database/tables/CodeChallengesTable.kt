package com.code_challenge_flux.core.services.database.tables

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.codewars.ChallengeSources
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object CodeChallengesTable : UUIDTable() {
    val name = varchar("name", 255)
    val description = text("description")
    val challengeSource = enumeration("source", ChallengeSources::class)
    val difficult = varchar("difficult", 20)
    val solution = text("solution")
    val userId = reference("user_id", UsersTable.id)
}