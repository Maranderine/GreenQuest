package de.hsb.greenquest.domain.model

import android.net.Uri

data class Plant(
    val name: String,
    val commonNames: List<String>,
    val species: String,
    val description: String,
    val imagePath: Uri?, // Datatype may need to be adjusted
    val favorite: Boolean
)
