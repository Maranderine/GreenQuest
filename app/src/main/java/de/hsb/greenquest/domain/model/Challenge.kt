package de.hsb.greenquest.domain.model

data class Challenge(
    val id: Int = 0,
    val description: String,
    val Plant: String,
    val requiredCount: Int,
    val progress: Int,
    val date: String?
) {
    val done: Boolean
        get() = this.progress >= this.requiredCount
}