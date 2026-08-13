package com.code_challenge_flux.core.services.utils

import org.jetbrains.exposed.v1.core.Column
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun uuid7(): Uuid = Uuid.generateV7()

fun Column<Uuid>.autoGenerateV7(): Column<Uuid> {
    defaultValueFun = ::uuid7
    return this
}