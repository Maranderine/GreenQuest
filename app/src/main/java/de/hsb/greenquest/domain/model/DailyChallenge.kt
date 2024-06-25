package de.hsb.greenquest.domain.model

data class DailyChallenge(
    val challengeId: Int,
    val description: String,
    val type: String,
    val requiredCount: Int,
    val progress: Int,
    val date: String?
) {
    val done: Boolean
        get() = this.progress >= this.requiredCount
}