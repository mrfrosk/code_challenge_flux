package com.code.challenge_flux.data.database.com.code_challenge_flux.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(val email: String, val username: String, val password: String)
