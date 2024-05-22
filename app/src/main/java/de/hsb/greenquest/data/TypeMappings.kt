package de.hsb.greenquest.data

import de.hsb.greenquest.Challenge

fun Challenge.toLocal() = LocalChallenge(
    id = id,
    description = description,
    Plant = Plant,
    requiredCount = requiredCount,
    progress = progress,
    date = date
)

fun List<Challenge>.toLocal() = map(Challenge::toLocal)
fun LocalChallenge.toExternal() = Challenge(
    id = id,
    description = description,
    Plant = Plant,
    requiredCount = requiredCount,
    progress = progress,
    date = date
)

fun List<LocalChallenge>.toExternal() = map(LocalChallenge::toExternal)