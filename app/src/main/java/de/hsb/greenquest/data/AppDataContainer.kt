package de.hsb.greenquest.data

import android.content.Context

interface AppContainer {
    val challengeRepository: ChallengeRepository
}

/**
 * [AppContainer] implementation that provides instance of [ChallengeRepositoryImpl]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val challengeRepository: ChallengeRepository by lazy {
        ChallengeRepositoryImpl(ChallengeDatabas.getDatabase(context).challengeDao())
    }
}