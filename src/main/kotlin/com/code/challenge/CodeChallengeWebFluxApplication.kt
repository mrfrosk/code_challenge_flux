package com.code.challenge

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CodeChallengeWebFluxApplication

fun main(args: Array<String>) {
	runApplication<CodeChallengeWebFluxApplication>(*args)
}
