package com.code.challenge_flux.services

import com.code.challenge_flux.data.database.dto.LoginDto
import com.code.challenge_flux.data.database.dto.UserDto
import com.code.challenge_flux.data.database.entities.UserEntity
import com.code.challenge_flux.data.database.tables.UsersTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.springframework.stereotype.Service

@Service
class UserService {

    suspend fun getUser(username: String): UserDto {
        return findUser(username).toDto()
    }

    suspend fun isExists(loginDto: LoginDto): Boolean {
        return UserEntity.find {
            (UsersTable.email eq loginDto.email) and (UsersTable.password eq loginDto.password)
        }.firstOrNull() != null
    }

    suspend fun createUser(userDto: UserDto): UserDto {
        return UserEntity.new {
            email = userDto.email
            username = userDto.username
            password = userDto.password
        }.toDto()
    }

    suspend fun updateUser(username: String, userDto: UserDto) {
        val user = findUser(username)
        user.email = userDto.email
        user.username = userDto.username
        user.password = userDto.password
    }

    suspend fun deleteUser(username: String) {
        val user = findUser(username)
        user.delete()
    }

    suspend fun isExists(email: String) = UserEntity.find {
        UsersTable.email eq email
    }.firstOrNull() != null

    suspend fun isExists(email: String, username: String) = UserEntity.find {
        (UsersTable.email eq email) or (UsersTable.username eq username)
    }.firstOrNull() != null

    private suspend fun findUser(username: String): UserEntity {
        return UserEntity.find {
            UsersTable.username eq username
        }.first()
    }
}