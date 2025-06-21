package com.dinarastepina.decomposedictionary.di

import com.dinarastepina.decomposedictionary.data.local.DictionaryDatabase
import com.dinarastepina.decomposedictionary.data.local.dao.RussianDao
import com.dinarastepina.decomposedictionary.data.local.getRoomDatabase
import com.dinarastepina.decomposedictionary.data.local.getRussianDao
import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepository
import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val dataModule = module {
    single<DictionaryDatabase> {
        getRoomDatabase(get())
    }
    
    single<RussianDao> {
        getRussianDao(get())
    }
    
    single<DictionaryRepository> {
        DictionaryRepositoryImpl(get())
    }
} 