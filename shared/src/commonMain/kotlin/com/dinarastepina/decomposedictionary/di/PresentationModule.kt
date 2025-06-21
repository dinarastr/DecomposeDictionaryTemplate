package com.dinarastepina.decomposedictionary.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DefaultDictionaryComponent
import com.dinarastepina.decomposedictionary.presentation.components.main.DefaultMainComponent
import com.dinarastepina.decomposedictionary.presentation.components.root.DefaultRootComponent
import com.dinarastepina.decomposedictionary.presentation.components.LessonsComponent
import com.dinarastepina.decomposedictionary.presentation.components.LessonsComponentFactory
import com.dinarastepina.decomposedictionary.presentation.components.root.RootComponent
import com.dinarastepina.decomposedictionary.presentation.components.TopicsComponent
import com.dinarastepina.decomposedictionary.presentation.components.TopicsComponentFactory
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DictionaryComponent
import com.dinarastepina.decomposedictionary.presentation.components.main.MainComponent
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStoreFactory
import org.koin.dsl.module

val presentationModule = module {
    
    single<StoreFactory> { DefaultStoreFactory() }
    
    factory { DictionaryStoreFactory(get(), get()) }
    
    factory<DictionaryComponent.Factory> {
        DefaultDictionaryComponent.Factory(get()) 
    }
    
    factory<TopicsComponent.Factory> { 
        TopicsComponentFactory(get()) 
    }
    
    factory<LessonsComponent.Factory> { 
        LessonsComponentFactory(get()) 
    }
    
    factory<MainComponent.Factory> { 
        DefaultMainComponent.Factory(
            dictionaryComponentFactory = get(),
            topicsComponentFactory = get(),
            lessonsComponentFactory = get()
        ) 
    }
    
    factory<RootComponent.Factory> { 
        DefaultRootComponent.Factory(
            mainComponentFactory = get()
        ) 
    }
} 