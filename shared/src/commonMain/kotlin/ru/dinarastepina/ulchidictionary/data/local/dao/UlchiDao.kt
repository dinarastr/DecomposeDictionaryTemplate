package ru.dinarastepina.ulchidictionary.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.dinarastepina.ulchidictionary.data.local.entity.UlchiWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UlchiDao {
    @Query("SELECT * FROM ulchi_russian ORDER BY id ASC LIMIT :pageSize OFFSET :offset")
    fun getAllWords(pageSize: Int, offset: Int): Flow<List<UlchiWordEntity>>

    @Query("SELECT * FROM ulchi_russian WHERE word LIKE :query || '%' ORDER BY id ASC LIMIT :pageSize OFFSET :offset")
    fun searchWords(query: String, pageSize: Int, offset: Int): Flow<List<UlchiWordEntity>>

    @Query("SELECT COUNT(*) FROM ulchi_russian")
    suspend fun getWordsCount(): Int

    @Query("SELECT COUNT(*) FROM ulchi_russian WHERE word LIKE :query || '%'")
    suspend fun getSearchResultsCount(query: String): Int
}