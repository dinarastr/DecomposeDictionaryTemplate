package com.dinarastepina.decomposedictionary.di

import androidx.room.RoomDatabase
import com.dinarastepina.decomposedictionary.data.local.DictionaryDataBase
import com.dinarastepina.decomposedictionary.data.local.getDatabaseBuilder
import org.koin.dsl.module

actual fun platformModule() = module {
    single<RoomDatabase.Builder<DictionaryDataBase>> {
        getDatabaseBuilder()
    }
}