package com.code.challenge_flux.data.database.tables

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object UsersTable: UUIDTable() {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
}