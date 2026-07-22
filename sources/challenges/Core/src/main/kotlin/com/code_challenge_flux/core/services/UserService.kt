package com.code_challenge_flux.core.services

import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.IdUserDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.LoginDto
import com.code.challenge_flux.data.database.com.code_challenge_flux.dto.UserDto
import com.code_challenge_flux.core.services.database.entities.User
import com.code_challenge_flux.core.services.database.tables.UsersTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService {

    /**
     * Метод возвращает пользователя
     * @throws NoSuchElementException
     */

    suspend fun getUser(username: String): IdUserDto {
        return findUser(username).toDto()
    }

    suspend fun getUser(userId: UUID): IdUserDto {
        return User.findById(userId)?.toDto()
            ?: throw NoSuchElementException("Пользователя с id $userId не сущесвует")
    }

    /**
     * Проверяет существование пользователя
     */

    suspend fun isExists(loginDto: LoginDto): Boolean {
        return User.find {
            (UsersTable.email eq loginDto.email) and (UsersTable.password eq loginDto.password)
        }.firstOrNull() != null
    }

    /**
     * Создаёт пользователя
     * @param userDto данные пользователя
     */
    suspend fun createUser(userDto: UserDto): IdUserDto {
        return User.new {
            email = userDto.email
            username = userDto.username
            password = userDto.password
        }.toDto()
    }

    /**
     * Обновление пользовтаеля
     * @param username текущее имя учётной записи
     * @param userDto данные для обновления
     */
    suspend fun updateUser(username: String, userDto: UserDto) {
        val user = findUser(username)
        user.email = userDto.email
        user.username = userDto.username
        user.password = userDto.password
    }

    /**
     * Удаление пользователя
     * @param username имя учетной записи
     */
    suspend fun deleteUser(username: String) {
        val user = findUser(username)
        user.delete()
    }

    /**
     * Проверяет существование пользователя по email
     * @param email электронная почта пользователя
     */
    suspend fun isExists(email: String) = User.find {
        UsersTable.email eq email
    }.firstOrNull() != null

    /**
     * Метод для поиска пользователя
     * @param username имя учётной записи
     * @throws NoSuchElementException
     */

    private suspend fun findUser(username: String): User {
        return User.find {
            UsersTable.username eq username
        }.firstOrNull() ?: throw NoSuchElementException("Пользователя с именем $username не сущесвует")
    }
}