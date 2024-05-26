package de.hsb.greenquest.data.repository

import de.hsb.greenquest.domain.model.Challenge
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity

fun Challenge.toLocal() = LocalChallengeEntity(
    id = id,
    description = description,
    Plant = Plant,
    requiredCount = requiredCount,
    progress = progress,
    date = date
)

fun List<Challenge>.toLocal() = map(Challenge::toLocal)
fun LocalChallengeEntity.toExternal() = Challenge(
    id = id,
    description = description,
    Plant = Plant,
    requiredCount = requiredCount,
    progress = progress,
    date = date
)

fun List<LocalChallengeEntity>.toExternal() = map(LocalChallengeEntity::toExternal)