package ru.dinarastepina.ulchidictionary.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.dinarastepina.ulchidictionary.data.local.entity.TextEntity

@Dao
interface TextsDao {
    @Query("SELECT * FROM ulchi_texts ORDER BY id ASC")
    suspend fun getAllTexts(): List<TextEntity>
}