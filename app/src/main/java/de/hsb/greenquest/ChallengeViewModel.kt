package de.hsb.greenquest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.hsb.greenquest.data.LocalChallenge
import de.hsb.greenquest.data.ChallengeRepository
import okhttp3.Challenge
import java.util.Date

class ChallengeViewModel(private val challengeRepository: ChallengeRepository) : ViewModel() {
    var challengesUiState by mutableStateOf(ChallengesUiState())
        private set

}

data class ChallengesUiState(
    val challenges: List<LocalChallenge> = listOf<LocalChallenge>()
)

data class Challenge(
    val id: Int = 0,
    val description: String,
    val Plant: String,
    val requiredCount: Int,
    val progress: Int,
    val done: Boolean,
    val date: Date

)
