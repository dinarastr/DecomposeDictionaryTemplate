package com.dinarastepina.decomposedictionary.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.dinarastepina.decomposedictionary.data.local.converter.WordTypeConverter
import com.dinarastepina.decomposedictionary.data.local.dao.RussianDao
import com.dinarastepina.decomposedictionary.data.local.entity.RussianWordEntity
import com.dinarastepina.decomposedictionary.data.local.entity.UlchiWordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [RussianWordEntity::class, UlchiWordEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(WordTypeConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun getRussianDao(): RussianDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<DictionaryDatabase> {
    override fun initialize(): DictionaryDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<DictionaryDatabase>
): DictionaryDatabase {
    return builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getRussianDao(appDatabase: DictionaryDatabase) = appDatabase.getRussianDao()