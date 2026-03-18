package com.code_challenge_flux.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import java.sql.BatchUpdateException
import java.util.NoSuchElementException


@ControllerAdvice
class ExceptionController: ResponseEntityExceptionHandler() {
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFoundException() = ResponseEntity.status(HttpStatus.NOT_FOUND).build<Nothing>()

    @ExceptionHandler(BatchUpdateException::class)
    fun handleConflictException() = ResponseEntity.status(HttpStatus.CONFLICT).build<Nothing>()
}