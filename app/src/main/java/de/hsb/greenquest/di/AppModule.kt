package de.hsb.greenquest.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.hsb.greenquest.data.local.ChallengeDatabas
import de.hsb.greenquest.data.local.dao.ChallengeDao
import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.data.local.utils.DataBaseConstants.GREEN_QUEST_DATABASE
import de.hsb.greenquest.data.network.PlantNetDataSource
import de.hsb.greenquest.data.repository.ChallengeRepositoryImpl
import de.hsb.greenquest.data.repository.PlantNetRepositoryImpl
import de.hsb.greenquest.data.repository.PlantPictureMediaStoreRepositoryImpl
import de.hsb.greenquest.domain.repository.ChallengeRepository
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import de.hsb.greenquest.domain.usecase.EventManager
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, ChallengeDatabas::class.java, GREEN_QUEST_DATABASE
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideEventManager(): EventManager{
        return EventManager()
    }

    @Provides
    @Singleton
    fun providePlantNetRepository(ds: PlantNetDataSource): PlantNetRepository {
        return PlantNetRepositoryImpl(ds)
    }

    @Provides
    @Singleton
    fun providePlantNetDataSource(): PlantNetDataSource {
        return PlantNetDataSource()
    }

    @Provides
    @Singleton
    fun providePlantPictureDao(db: ChallengeDatabas) = db.plantPictureDao()

    @Provides
    @Singleton
    fun providePlantPictureEntity() = PlantPictureEntity()

//    @Provides
//    @Singleton
//    fun providePlantPictureRepository(dao: PlantPictureDao): PlantPictureRepository {
//        return PlantPictureRepositoryImpl(dao)
//    }

    @Provides
    @Singleton
    fun providePlantPictureRepository(@ApplicationContext context: Context, dao: PlantPictureDao): PlantPictureRepository {
        return PlantPictureMediaStoreRepositoryImpl(context, dao)
    }

    @Provides
    @Singleton
    fun provideTakePictureUseCase(plantPictureRepository: PlantPictureRepository): TakePictureUseCase {
        return TakePictureUseCase(plantPictureRepository)
    }

    @Provides
    @Singleton
    fun provideChallengeDao(db: ChallengeDatabas) = db.challengeDao()

    @Provides
    @Singleton
    fun provideChallengeRepository(dao: ChallengeDao): ChallengeRepository {
        return ChallengeRepositoryImpl(dao)
    }
}