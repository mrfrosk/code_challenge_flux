package services

import com.code.challenge_flux.data.database.dto.codewars.ChallengeSources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import services.challenge_sources.IChallengeSource
import services.challenge_sources.codewars.CodeWarsSource

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