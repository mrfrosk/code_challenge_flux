package com.code.challenge_flux.data.database.entities

import com.code.challenge_flux.data.database.dto.CreateUserDto
import com.code.challenge_flux.data.database.dto.UserDto
import com.code.challenge_flux.data.database.tables.UsersTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.*

class UserEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var email by UsersTable.email
    var username by UsersTable.username
    var password by UsersTable.password

    fun toDto(): UserDto = UserDto(
        id = id.value,
        email = email,
        username = username,
        password = password
    )

}