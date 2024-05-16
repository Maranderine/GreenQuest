package de.hsb.greenquest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocalChallenge::class], version = 1, exportSchema = false)
abstract class ChallengeDatabas: RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao

    companion object {
        @Volatile
        private var Instance: ChallengeDatabas? = null

        fun getDatabase(context: Context): ChallengeDatabas {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ChallengeDatabas::class.java, "challenge_database")
                    .build()
                    .also{ Instance = it }
            }
        }
    }
}