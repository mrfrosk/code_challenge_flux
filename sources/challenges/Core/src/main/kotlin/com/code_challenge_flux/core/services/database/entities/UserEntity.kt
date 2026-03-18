package com.code_challenge_flux.core.services.database.entities

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.IdUserDto
import com.code_challenge_flux.core.services.database.tables.UsersTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var email by UsersTable.email
    var username by UsersTable.username
    var password by UsersTable.password

    fun toDto(): IdUserDto = IdUserDto(
        id = id.value,
        email = email,
        username = username,
        password = password
    )

}