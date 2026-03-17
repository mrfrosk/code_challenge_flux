package com.code.challenge_flux.data.database.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserDto(val email: String, val username: String, val password: String)
