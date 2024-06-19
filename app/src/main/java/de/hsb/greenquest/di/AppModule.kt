package de.hsb.greenquest.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.hsb.greenquest.data.local.ChallengeCardImageInternalStorageLoader
import de.hsb.greenquest.data.local.ChallengeDatabase
import de.hsb.greenquest.data.local.dao.ActiveDailyChallengeDao
import de.hsb.greenquest.data.local.dao.ChallengeCardDao
import de.hsb.greenquest.data.local.dao.DailyChallengeDao
import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.data.local.utils.DataBaseConstants.GREEN_QUEST_DATABASE
import de.hsb.greenquest.data.network.PlantNetDataSource
import de.hsb.greenquest.data.repository.DailyChallengeRepositoryImpl
import de.hsb.greenquest.data.local.mediastore.PlantPictureMediaStoreLoader
import de.hsb.greenquest.data.network.ChallengeCardDataSource
import de.hsb.greenquest.data.network.ChallengeCardPicturesDataSource
import de.hsb.greenquest.data.repository.AchievementsRepositoryImpl
import de.hsb.greenquest.data.repository.ChallengeCardRepositoryImpl
import de.hsb.greenquest.data.repository.PlantPictureRepositoryImpl
import de.hsb.greenquest.data.repository.PlantNetRepositoryImpl
import de.hsb.greenquest.domain.repository.ChallengeCardRepository
import de.hsb.greenquest.domain.repository.DailyChallengeRepository
import de.hsb.greenquest.domain.repository.PlantNetRepository
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import de.hsb.greenquest.domain.usecase.TakePictureUseCase
import javax.annotation.Nullable
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ChallengeDatabase::class.java, GREEN_QUEST_DATABASE)
            .createFromAsset("database/dailyChallenges.db")
            //fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providePlantPictureDao(db: ChallengeDatabase) = db.plantPictureDao()

    @Provides
    @Singleton
    fun provideChallengeDao(db: ChallengeDatabase) = db.dailyChallengeDao()

    @Provides
    @Singleton
    fun provideActiveChallengeDao(db: ChallengeDatabase) = db.activeDailyChallengeDao()

    @Provides
    @Singleton
    fun provideChallengeCardDao(db: ChallengeDatabase) = db.challengeCardDao()


    @Provides
    @Singleton
    fun providePlantNetRepository(ds: PlantNetDataSource): PlantNetRepository {
        return PlantNetRepositoryImpl(ds)
    }

    @Provides
    @Singleton
    fun provideChallengeCardRepository(
        firebaseApp: FirebaseApp?,
        challengeCardDao: ChallengeCardDao,
        challengeCardDataSource: ChallengeCardDataSource,
        challengeCardImageInternalStorageLoader: ChallengeCardImageInternalStorageLoader,
        challengeCardPicturesDataSource: ChallengeCardPicturesDataSource
    ): ChallengeCardRepository{
        return ChallengeCardRepositoryImpl(firebaseApp, challengeCardDao, challengeCardImageInternalStorageLoader, challengeCardDataSource, challengeCardPicturesDataSource)
    }

    @Provides
    @Singleton
    fun provideAchivemetsRepository(
        @ApplicationContext context: Context,
        dailyChallengeRepository: DailyChallengeRepository,
        challengeCardRepository: ChallengeCardRepository
    ): AchievementsRepositoryImpl{
        return AchievementsRepositoryImpl(dailyChallengeRepository, challengeCardRepository, context)
    }

    @Provides
    @Singleton
    fun providePlantPictureRepository(
        dao: PlantPictureDao,
        plantPictureMediaStoreLoader: PlantPictureMediaStoreLoader
    ): PlantPictureRepository {
        return PlantPictureRepositoryImpl(
            plantPictureDao = dao,
            plantPictureMediaStoreLoader = plantPictureMediaStoreLoader
        )
    }

    @Provides
    @Singleton
    fun provideDailyChallengeRepository(dailyChallengeDao: DailyChallengeDao, activeDailyChallengeDao: ActiveDailyChallengeDao): DailyChallengeRepository {
        return DailyChallengeRepositoryImpl(dailyChallengeDao = dailyChallengeDao, activeDailyChallengeDao = activeDailyChallengeDao)
    }

    @Provides
    @Singleton
    fun providePlantNetDataSource(): PlantNetDataSource {
        return PlantNetDataSource()
    }

    @Provides
    @Singleton
    fun provideChallengeCardDataSource(): ChallengeCardDataSource{
        return ChallengeCardDataSource()
    }

    @Provides
    @Singleton
    fun provideChallengeCardPicturesDataSource(): ChallengeCardPicturesDataSource{
        return ChallengeCardPicturesDataSource()
    }

    @Provides
    @Singleton
    fun providePlantPictureEntity() = PlantPictureEntity()



    @Provides
    @Singleton
    fun providePlantPictureMediaStoreLoader(@ApplicationContext context: Context): PlantPictureMediaStoreLoader {
        return PlantPictureMediaStoreLoader(context)
    }

    @Provides
    @Singleton
    fun provideChallengeCardImageLoader(@ApplicationContext context: Context): ChallengeCardImageInternalStorageLoader{
        return ChallengeCardImageInternalStorageLoader(context)
    }
    @Provides
    @Singleton
    fun provideTakePictureUseCase(plantPictureRepository: PlantPictureRepository): TakePictureUseCase {
        return TakePictureUseCase(plantPictureRepository)
    }

    @Provides
    @Singleton
    fun provideFirebase(@ApplicationContext context: Context): FirebaseApp? {
        return FirebaseApp.initializeApp(context)
    }

}