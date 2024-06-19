package de.hsb.greenquest.domain.model

import java.io.File

data class challengeCard (
    val id: String,
    val imgPath: String,
    val name: String,
    val hint: String,
    val location: String
)