package com.dinarastepina.decomposedictionary.di

import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepository
import com.dinarastepina.decomposedictionary.data.repository.DictionaryRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val dataModule = module {
    single<DictionaryRepository> { DictionaryRepositoryImpl() }
} 