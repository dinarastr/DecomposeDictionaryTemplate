package com.dinarastepina.decomposedictionary.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.dinarastepina.decomposedictionary.data.local.DictionaryDatabase
import com.dinarastepina.decomposedictionary.data.local.datastore.createDataStore
import com.dinarastepina.decomposedictionary.data.local.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<DictionaryDatabase>> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
    }
    single<DataStore<Preferences>> {
        createDataStore()
    }
}