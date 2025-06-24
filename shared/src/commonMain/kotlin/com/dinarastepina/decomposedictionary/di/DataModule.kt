package com.dinarastepina.decomposedictionary.di

import com.dinarastepina.decomposedictionary.data.local.DictionaryDatabase
import com.dinarastepina.decomposedictionary.data.local.dao.PhraseBookDao
import com.dinarastepina.decomposedictionary.data.local.dao.RussianDao
import com.dinarastepina.decomposedictionary.data.local.dao.TextsDao
import com.dinarastepina.decomposedictionary.data.local.dao.UlchiDao
import com.dinarastepina.decomposedictionary.data.local.getPhraseBookDao
import com.dinarastepina.decomposedictionary.data.local.getRoomDatabase
import com.dinarastepina.decomposedictionary.data.local.getRussianDao
import com.dinarastepina.decomposedictionary.data.local.getTextsDao
import com.dinarastepina.decomposedictionary.data.local.getUlchiDao
import com.dinarastepina.decomposedictionary.domain.repository.DataStoreRepository
import com.dinarastepina.decomposedictionary.data.repository.DataStoreRepositoryImpl
import com.dinarastepina.decomposedictionary.domain.repository.DictionaryRepository
import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepositoryImpl
import com.dinarastepina.decomposedictionary.data.repository.PhraseBookRepositoryImpl
import com.dinarastepina.decomposedictionary.data.repository.TextsRepositoryImpl
import com.dinarastepina.decomposedictionary.domain.repository.PhraseBookRepository
import com.dinarastepina.decomposedictionary.domain.repository.TextsRepository
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
    single<UlchiDao> {
        getUlchiDao(get())
    }
    single<PhraseBookDao> {
        getPhraseBookDao(get())
    }
    single<TextsDao> {
        getTextsDao(get())
    }
    single<DictionaryRepository> {
        DictionaryRepositoryImpl(get(), get())
    }
    single<DataStoreRepository> {
        DataStoreRepositoryImpl(get())
    }
    single<PhraseBookRepository> {
        PhraseBookRepositoryImpl(get())
    }
    single<TextsRepository> {
        TextsRepositoryImpl(get())
    }
} 