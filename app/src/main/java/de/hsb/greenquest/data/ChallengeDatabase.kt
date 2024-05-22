package de.hsb.greenquest.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.reflect.KParameter

@Database(entities = [LocalChallenge::class], version = 2, exportSchema = true)
abstract class ChallengeDatabas: RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao

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