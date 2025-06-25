package ru.dinarastepina.ulchidictionary.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import ru.dinarastepina.ulchidictionary.data.local.DictionaryDatabase
import ru.dinarastepina.ulchidictionary.data.local.datastore.createDataStore
import ru.dinarastepina.ulchidictionary.data.local.getDatabaseBuilder
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