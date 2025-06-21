package com.dinarastepina.decomposedictionary.di

import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepository
import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    single<DictionaryRepository> { DictionaryRepositoryImpl() }
} 