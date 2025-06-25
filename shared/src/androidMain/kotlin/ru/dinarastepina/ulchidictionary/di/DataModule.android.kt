package ru.dinarastepina.ulchidictionary.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import ru.dinarastepina.ulchidictionary.data.local.datastore.createDataStore
import ru.dinarastepina.ulchidictionary.data.local.getDatabaseBuilder
import org.koin.dsl.module
import ru.dinarastepina.ulchidictionary.data.local.DictionaryDatabase

actual fun platformModule() = module {
    single<RoomDatabase.Builder<DictionaryDatabase>> {
        getDatabaseBuilder(get())
    }
    single<DataStore<Preferences>> {
        createDataStore(get())
    }
}