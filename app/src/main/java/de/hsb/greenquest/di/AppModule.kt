package de.hsb.greenquest.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.hsb.greenquest.data.local.GreenQuestDB
import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import de.hsb.greenquest.data.local.utils.DataBaseConstants.GREEN_QUEST_DATABASE
import de.hsb.greenquest.data.repository.PlantPictureRepositoryImpl
import de.hsb.greenquest.domain.repository.PlantPictureRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, GreenQuestDB::class.java, GREEN_QUEST_DATABASE
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun providePlantPictureDao(db: GreenQuestDB) = db.plantPictureDao()

    @Provides
    @Singleton
    fun providePlantPictureEntity() = PlantPictureEntity()

    @Provides
    @Singleton
    fun providePlantPictureRepository(dao: PlantPictureDao): PlantPictureRepository {
        return PlantPictureRepositoryImpl(dao)
    }
}