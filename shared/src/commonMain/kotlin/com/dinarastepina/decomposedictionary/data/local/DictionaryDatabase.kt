package com.dinarastepina.decomposedictionary.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.dinarastepina.decomposedictionary.data.local.dao.RussianDao
import com.dinarastepina.decomposedictionary.data.local.entity.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [
    WordEntity::class,
 ], version = 4, exportSchema = true)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class DictionaryDataBase: RoomDatabase() {
    abstract fun getRussianDao(): RussianDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<DictionaryDataBase> {
    override fun initialize(): DictionaryDataBase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<DictionaryDataBase>
): DictionaryDataBase {
    return builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getRussianDao(appDatabase: DictionaryDataBase) = appDatabase.getRussianDao()