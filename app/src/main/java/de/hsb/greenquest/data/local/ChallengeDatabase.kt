package de.hsb.greenquest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.hsb.greenquest.data.local.dao.ChallengeDao
import de.hsb.greenquest.data.local.dao.PlantPictureDao
import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import de.hsb.greenquest.data.local.entity.PlantPictureEntity
import androidx.room.AutoMigration

@Database(entities = [LocalChallengeEntity::class, PlantPictureEntity::class], version = 1, exportSchema = true/*, autoMigrations = [
    AutoMigration (from = 1, to = 2)
]*/)
abstract class ChallengeDatabas: RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
    abstract fun plantPictureDao(): PlantPictureDao

    companion object {
        @Volatile
        private var Instance: ChallengeDatabas? = null

        fun getDatabase(context: Context): ChallengeDatabas {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ChallengeDatabas::class.java, "challenge_database")
                    /*.addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate the database with initial data
                            // For example:
                            val userDao = Instance?.challengeDao()
                            //userDao?.insert(User("John Doe"))
                            //userDao?.insert(User("Jane Smith"))
                            userDao?.clearAll()
                        }
                    })*/
                    .build()
                    .also{ Instance = it }
            }
        }
    }
}