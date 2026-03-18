package com.code.challenge_flux.controllers

import com.code.challenge_flux.data.database.dto.CodeChallengeDto
import com.code.challenge_flux.data.database.dto.codewars.ChallengeSources
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import services.ChallengeService

@RestController
@RequestMapping(Mapping.CHALLENGE)
class ChallengeController {

    @Autowired
    private lateinit var challengeService: ChallengeService

    @PostMapping("/{source}/load/{username}")
    suspend fun updateFromCodeWars(
        @PathVariable source: ChallengeSources,
        @PathVariable username: String
    ) {
        return suspendTransaction {
            challengeService.addChallengeFromSource(username, source)
        }
    }

    @GetMapping("/{source}/{username}/{name}")
    suspend fun getChallenge(
        @PathVariable("source") source: ChallengeSources,
        @PathVariable("username") username: String,
        @PathVariable("name") name: String
    ): CodeChallengeDto {
        return suspendTransaction {
            challengeService.getChallenge(username, source, name)
        }
    }

    @GetMapping("/{source}/{username}", produces = ["application/json"])
    suspend fun getCodeWarsChallenges(
        @PathVariable("source") source: ChallengeSources,
        @PathVariable username: String,
    ): ResponseEntity<*> {
        val challenge = suspendTransaction {
            challengeService.getChallenges(username, source)
        }
        return ResponseEntity.ok(challenge)
    }

    @GetMapping("/{username}", produces = ["application/json"])
    suspend fun getCodeWarsChallenges(
        @PathVariable username: String,
    ): ResponseEntity<*> {
        val challenge = suspendTransaction {
            challengeService.getChallenges(username)
        }
        return ResponseEntity.ok(challenge)
    }

    @PostMapping("*/{username}", produces = ["application/json"])
    suspend fun createChallenge(
        @PathVariable username: String,
        @RequestBody challengeData: String,
    ): ResponseEntity<*> {
        val challenge = suspendTransaction {
            val challenge = Json.decodeFromString<CodeChallengeDto>(challengeData)
            challengeService.createChallenge(username, challenge)
        }
        return ResponseEntity.ok(challenge)
    }

    @PutMapping("*/{username}")
    suspend fun updateChallenge(
        @PathVariable username: String,
        @RequestBody challengeData: String,
    ): ResponseEntity<*> {
        val challenge = suspendTransaction {
            val challenge = Json.decodeFromString<CodeChallengeDto>(challengeData)
            challengeService.updateChallenge(username, challenge)
        }

        return ResponseEntity.ok(challenge)
    }

    @DeleteMapping("*/{username}/{name}")
    suspend fun deleteChallenge(
        @PathVariable username: String,
        @PathVariable name: String,
    ): ResponseEntity<*> {
        suspendTransaction {
            challengeService.deleteChallenge(username, name)
        }
        return ResponseEntity.noContent().build<Nothing>()
    }

}