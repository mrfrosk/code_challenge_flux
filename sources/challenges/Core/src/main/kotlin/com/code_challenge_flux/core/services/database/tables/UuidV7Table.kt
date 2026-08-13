package com.code_challenge_flux.core.services.database.tables

import com.code_challenge_flux.core.services.utils.autoGenerateV7
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import kotlin.uuid.Uuid


open class UuidV7Table(name: String = "", columnName: String = "id") : IdTable<Uuid>(name) {
    override val id: Column<EntityID<Uuid>> = uuid(columnName).autoGenerateV7().entityId()
}

