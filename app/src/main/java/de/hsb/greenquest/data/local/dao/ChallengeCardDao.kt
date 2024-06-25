package de.hsb.greenquest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.hsb.greenquest.data.local.entity.ActiveDailyChallengeEntity
import de.hsb.greenquest.data.local.entity.ChallengeCardEntity
import kotlinx.coroutines.flow.Flow


// DAO FOR MANAGING ACITVE CHALLENGE CARD DATA
@Dao
interface ChallengeCardDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    suspend fun insert(challenge: ChallengeCardEntity)

    @Update
    suspend fun update(challenge: ChallengeCardEntity)

    @Query("DELETE FROM activeChallengeCards WHERE id = :challengeId")
    suspend fun delete(challengeId: String)

    /*val id: String,
    val name: String,
    val imagePath: String,
    val location: String,
    val hint: String,*/
    @Query("""INSERT INTO activeChallengeCards ( id, name, imagePath, location, hint )
        VALUES (:id, :name, :imagePath, :location, :hint)
    """)
    suspend fun insertInto(id: String, name: String, imagePath: String, location: String, hint: String)

    @Query("DELETE from activeChallengeCards")
    fun clearAll()

    @Query("SELECT * from activeChallengeCards")
    fun getChallengeCardsDataStream(): Flow<List<ChallengeCardEntity>>

    @Query("SELECT * from activeChallengeCards")
    suspend fun getChallengeCardsData(): List<ChallengeCardEntity>
}