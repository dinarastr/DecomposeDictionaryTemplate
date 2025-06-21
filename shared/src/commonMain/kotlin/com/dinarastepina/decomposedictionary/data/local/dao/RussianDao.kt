package com.dinarastepina.decomposedictionary.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.dinarastepina.decomposedictionary.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RussianDao {

    @Query("SELECT * FROM russian_ulchi ORDER BY id ASC LIMIT :pageSize OFFSET :offset")
    fun getAllWords(pageSize: Int, offset: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM russian_ulchi WHERE word LIKE :query || '%' ORDER BY id ASC LIMIT :pageSize OFFSET :offset")
    fun searchWords(query: String, pageSize: Int, offset: Int): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM russian_ulchi")
    suspend fun getWordsCount(): Int

    @Query("SELECT COUNT(*) FROM russian_ulchi WHERE word LIKE :query || '%'")
    suspend fun getSearchResultsCount(query: String): Int
}