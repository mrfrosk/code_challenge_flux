package com.code.challenge_flux.controllers

import com.code.challenge_flux.data.database.dto.UserDto
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import services.UserService


@RestController
@CrossOrigin
@RequestMapping(Mapping.USER)
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/{username}", produces = ["application/json"])
    suspend fun getUser(@PathVariable username: String): ResponseEntity<*> {
        val user = suspendTransaction {
            userService.getUser(username)
        }
        val entity = ResponseEntity.ok(user)
        return entity
    }

    @PostMapping
    suspend fun createUser(
        @RequestBody
        user: UserDto
    ): ResponseEntity<*> {
        val data = suspendTransaction {
            userService.createUser(user)
        }
        return ResponseEntity.ok(Json.encodeToString(data))
    }


    @PutMapping("/{username}", produces = ["application/json"])
    suspend fun updateUser(@PathVariable username: String, @RequestBody update: String): ResponseEntity<*> {
        val updateData = Json.decodeFromString<UserDto>(update)
        suspendTransaction {
            userService.updateUser(username, updateData)
        }
        return ResponseEntity.ok(updateData)
    }

    @DeleteMapping("/{username}")
    suspend fun deleteUser(@PathVariable username: String): ResponseEntity<Nothing> {
        suspendTransaction {
            userService.deleteUser(username)
        }
        return ResponseEntity.noContent().build<Nothing>()
    }


}