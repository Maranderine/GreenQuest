package de.hsb.greenquest.domain.model

data class Plant(
    val name: String,
    val description: String,
    val image: Int, // Datatype may need to be adjusted
    val favorite: Boolean
)
