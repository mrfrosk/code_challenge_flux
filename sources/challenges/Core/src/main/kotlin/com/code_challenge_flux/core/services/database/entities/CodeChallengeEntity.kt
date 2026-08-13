package com.code_challenge_flux.core.services.database.entities

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.CodeChallengeDto
import com.code_challenge_flux.core.services.database.tables.CodeChallengesTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*
import kotlin.uuid.Uuid

class CodeChallengeEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<CodeChallengeEntity>(CodeChallengesTable)

    var name by CodeChallengesTable.name
    var description by CodeChallengesTable.description
    var challengeSource by CodeChallengesTable.challengeSource
    var difficult by CodeChallengesTable.difficult
    var solution by CodeChallengesTable.solution
    var user by UserEntity referencedOn CodeChallengesTable.userId

    fun toDto() = CodeChallengeDto(
        name,
        description,
        challengeSource,
        difficult,
        solution
    )

    fun updateFromDto(updateData: CodeChallengeDto) {
        name = updateData.name
        description = updateData.description
        challengeSource = updateData.challengeSource
        difficult = updateData.difficult
        solution = updateData.solution
    }
}