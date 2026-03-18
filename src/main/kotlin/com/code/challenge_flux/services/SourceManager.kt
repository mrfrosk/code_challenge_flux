package com.code.challenge_flux.services

import com.code.challenge_flux.data.challenge_sources.ChallengeSources
import com.code.challenge_flux.data.challenge_sources.IChallengeSource
import com.code.challenge_flux.data.challenge_sources.codewars.CodeWarsSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SourceManager {
    @Autowired
    private lateinit var codeWarsSource: CodeWarsSource

    suspend fun getSource(source: ChallengeSources): IChallengeSource {
        return when (source) {
            ChallengeSources.CodeWars -> codeWarsSource
            ChallengeSources.LeetCode -> throw NoSuchElementException("Поддрежка LeetCode в разработке")
            else -> {
                throw NoSuchElementException("Данного источника задач не сущесвует")
            }
        }
    }
}