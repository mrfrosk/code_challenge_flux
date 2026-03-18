package com.code.challenge_flux

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
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
@SpringBootTest
class CodeChallengeWebFluxApplicationTests {

	@Test
	fun contextLoads() {
	}

}
