package de.hsb.greenquest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hsb.greenquest.data.local.dao.ActiveDailyChallengeDao
import de.hsb.greenquest.data.local.dao.ChallengeCardDao
import de.hsb.greenquest.data.local.dao.DailyChallengeDao
import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.ActiveDailyChallengeEntity
import de.hsb.greenquest.data.local.entity.ChallengeCardEntity
import de.hsb.greenquest.data.local.entity.CommonNameEntity
import de.hsb.greenquest.data.local.entity.DailyChallengeEntity
import de.hsb.greenquest.data.local.entity.PlantPictureEntity

@Database(
    entities = [ActiveDailyChallengeEntity::class, DailyChallengeEntity::class, PlantPictureEntity::class, ChallengeCardEntity::class, CommonNameEntity::class],
    version = 1, exportSchema = false)
abstract class ChallengeDatabase: RoomDatabase() {
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun plantPictureDao(): PlantPictureDao
    abstract fun activeDailyChallengeDao(): ActiveDailyChallengeDao
    abstract fun challengeCardDao(): ChallengeCardDao

    companion object {
        @Volatile
        private var Instance: ChallengeDatabase? = null

        fun getDatabase(context: Context): ChallengeDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ChallengeDatabase::class.java, "challenge_database")
                    .build()
                    .also{ Instance = it }
            }
        }
    }
}