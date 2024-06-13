package de.hsb.greenquest.domain.model

import java.io.File

data class challengeCard (
    val img: File,
    val name: String,
    val hint: String,
    val location: String
)