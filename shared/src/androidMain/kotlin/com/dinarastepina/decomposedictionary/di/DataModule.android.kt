package com.dinarastepina.decomposedictionary.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.dinarastepina.decomposedictionary.data.local.DictionaryDatabase
import com.dinarastepina.decomposedictionary.data.local.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<DictionaryDatabase>> {
        getDatabaseBuilder(get())
            .setDriver(BundledSQLiteDriver())
    }
}