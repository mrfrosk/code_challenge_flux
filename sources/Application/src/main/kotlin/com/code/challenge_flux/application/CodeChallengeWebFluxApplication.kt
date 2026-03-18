package com.code.challenge_flux.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.WebFluxConfigurationSupport

@SpringBootApplication(
    scanBasePackages = [
        "com.code_challenge_flux.core",
        "com.code_challenge_flux.dto",
        "com.code_challenge_flux.api",
        "com.code_challenge_flux.application",
    ],
    scanBasePackageClasses = [
        WebFluxConfigurationSupport::class,
    ],

    )

class CodeChallengeWebFluxApplication

fun main(args: Array<String>) {
    runApplication<CodeChallengeWebFluxApplication>(*args)
}
