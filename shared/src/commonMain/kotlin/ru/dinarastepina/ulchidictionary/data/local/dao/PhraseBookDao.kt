package ru.dinarastepina.ulchidictionary.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.dinarastepina.ulchidictionary.data.local.entity.PhraseEntity
import ru.dinarastepina.ulchidictionary.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseBookDao {
    @Query("SELECT * FROM ulchi_themes ORDER BY id ASC")
    suspend fun getAllTopics(): List<TopicEntity>

    @Query("SELECT * FROM ulchi_phrases WHERE topicId = :topicId")
    suspend fun getPhrasesByTopic(topicId: Int): List<PhraseEntity>

    @Query("SELECT * FROM ulchi_phrases ORDER BY id ASC LIMIT :pageSize OFFSET :offset")
    fun getAllPhrases(pageSize: Int, offset: Int): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM ulchi_phrases WHERE russian LIKE :query || '%'  OR ulchi LIKE :query || '%' ORDER BY id ASC LIMIT :pageSize OFFSET :offset")
    fun searchPhrases(query: String, pageSize: Int, offset: Int): Flow<List<PhraseEntity>>

    @Query("SELECT COUNT(*) FROM ulchi_phrases")
    suspend fun getPhrasesCount(): Int

    @Query("SELECT COUNT(*) FROM ulchi_phrases WHERE russian LIKE :query || '%' OR ulchi LIKE :query || '%'")
    suspend fun getSearchResultsCount(query: String): Int
}