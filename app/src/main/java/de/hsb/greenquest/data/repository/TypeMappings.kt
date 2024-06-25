package de.hsb.greenquest.data.repository

import de.hsb.greenquest.data.local.entity.ActiveDailyChallengeEntity
import de.hsb.greenquest.data.local.entity.ChallengeCardEntity
import de.hsb.greenquest.data.local.entity.DailyChallengeEntity
import de.hsb.greenquest.domain.model.DailyChallenge
import de.hsb.greenquest.domain.model.challengeCard
import de.hsb.greenquest.ui.screen.ChallengeCard

val formater = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")

val today = java.time.LocalDateTime.now().format(formater);


// FOR DAILY CHALLENGES
fun DailyChallenge.toActive() = ActiveDailyChallengeEntity(
    challengeId = challengeId,
    progress = progress,
    date = date
)

fun List<DailyChallenge>.toActive() = map(DailyChallenge::toActive)

fun DailyChallengeEntity.toChallenge() = DailyChallenge(
    challengeId = id,
    description = description,
    type = type,
    requiredCount = requiredCount,
    progress = 0,
    date = today
)

fun List<DailyChallengeEntity>.toChallenge() = map(DailyChallengeEntity::toChallenge)

fun ChallengeCardEntity.toChallengeCard() = challengeCard(
    id = id,
    name = name,
    location = location,
    hint = hint,
    imgPath = imagePath
)

fun List<ChallengeCardEntity>.toChallengeCard() = map(ChallengeCardEntity::toChallengeCard)

fun challengeCard.toChallengeCardEntity() = ChallengeCardEntity(
    id = id,
    name = name,
    location = location,
    hint = hint,
    imagePath = imgPath
)

fun List<challengeCard>.toChallengeCardEntity() = map(challengeCard::toChallengeCardEntity)