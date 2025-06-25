package ru.dinarastepina.ulchidictionary.di

import ru.dinarastepina.ulchidictionary.data.local.DictionaryDatabase
import ru.dinarastepina.ulchidictionary.data.local.dao.PhraseBookDao
import ru.dinarastepina.ulchidictionary.data.local.dao.RussianDao
import ru.dinarastepina.ulchidictionary.data.local.dao.TextsDao
import ru.dinarastepina.ulchidictionary.data.local.dao.UlchiDao
import ru.dinarastepina.ulchidictionary.data.local.getPhraseBookDao
import ru.dinarastepina.ulchidictionary.data.local.getRoomDatabase
import ru.dinarastepina.ulchidictionary.data.local.getRussianDao
import ru.dinarastepina.ulchidictionary.data.local.getTextsDao
import ru.dinarastepina.ulchidictionary.data.local.getUlchiDao
import ru.dinarastepina.ulchidictionary.domain.repository.DataStoreRepository
import ru.dinarastepina.ulchidictionary.data.repository.DataStoreRepositoryImpl
import ru.dinarastepina.ulchidictionary.domain.repository.DictionaryRepository
import ru.dinarastepina.ulchidictionary.data.repository.DictionaryRepositoryImpl
import ru.dinarastepina.ulchidictionary.data.repository.PhraseBookRepositoryImpl
import ru.dinarastepina.ulchidictionary.data.repository.TextsRepositoryImpl
import ru.dinarastepina.ulchidictionary.domain.repository.PhraseBookRepository
import ru.dinarastepina.ulchidictionary.domain.repository.TextsRepository
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