package com.dinarastepina.decomposedictionary.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.dinarastepina.decomposedictionary.data.local.converter.UlchiTypeConverter
import com.dinarastepina.decomposedictionary.data.local.converter.WordTypeConverter
import com.dinarastepina.decomposedictionary.data.local.dao.PhraseBookDao
import com.dinarastepina.decomposedictionary.data.local.dao.RussianDao
import com.dinarastepina.decomposedictionary.data.local.dao.TextsDao
import com.dinarastepina.decomposedictionary.data.local.dao.UlchiDao
import com.dinarastepina.decomposedictionary.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        RussianWordEntity::class,
        UlchiWordEntity::class,
        PhraseEntity::class,
        TopicEntity::class,
        TextEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(WordTypeConverter::class, UlchiTypeConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun getRussianDao(): RussianDao
    abstract fun getUlchiDao(): UlchiDao
    abstract fun getPhraseBookDao(): PhraseBookDao
    abstract fun getTextsDao(): TextsDao
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
fun getUlchiDao(appDatabase: DictionaryDatabase) = appDatabase.getUlchiDao()
fun getPhraseBookDao(appDatabase: DictionaryDatabase) = appDatabase.getPhraseBookDao()
fun getTextsDao(appDatabase: DictionaryDatabase) = appDatabase.getTextsDao()
