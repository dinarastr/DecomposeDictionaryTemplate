package com.dinarastepina.decomposedictionary.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.dinarastepina.decomposedictionary.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RussianDao {

    @Query("SELECT * FROM russian_to_nanay ORDER BY id ASC LIMIT :pageSize OFFSET :offset")
    fun readAllRussianWords(pageSize: Int, offset: Int): Flow<List<WordEntity>>

    @Query("SELECT * FROM russian_to_nanay WHERE word LIKE :name || '%' LIMIT :pageSize OFFSET :offset")
    fun searchRussianWords(name: String, pageSize: Int, offset: Int): Flow<List<WordEntity>>
}