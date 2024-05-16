package de.hsb.greenquest.domain.model

data class Plant(
    val name: String,
    val description: String,
    val imagePath: String, // Datatype may need to be adjusted
    val favorite: Boolean
)
