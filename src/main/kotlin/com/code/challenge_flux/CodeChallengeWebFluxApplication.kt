package com.code.challenge_flux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.WebFluxConfigurationSupport

@SpringBootApplication(
    scanBasePackages = [
        "services",
        "dto",
        "utils",
    "com.code.challenge_flux"
    ],
    scanBasePackageClasses = [
        WebFluxConfigurationSupport::class,
    ],

    )

class CodeChallengeWebFluxApplication

fun main(args: Array<String>) {
    runApplication<CodeChallengeWebFluxApplication>(*args)
}
