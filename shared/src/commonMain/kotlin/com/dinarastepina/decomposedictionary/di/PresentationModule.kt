package com.dinarastepina.decomposedictionary.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DefaultDictionaryComponent
import com.dinarastepina.decomposedictionary.presentation.components.dictionary.DictionaryComponent
import com.dinarastepina.decomposedictionary.presentation.components.main.DefaultMainComponent
import com.dinarastepina.decomposedictionary.presentation.components.main.MainComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases.DefaultPhrasesComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.phrases.PhrasesComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.search.DefaultSearchComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.search.SearchComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics.DefaultTopicsComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics.DefaultTopicsListComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics.TopicsComponent
import com.dinarastepina.decomposedictionary.presentation.components.phrasebook.topics.TopicsListComponent
import com.dinarastepina.decomposedictionary.presentation.components.root.DefaultRootComponent
import com.dinarastepina.decomposedictionary.presentation.components.root.RootComponent
import com.dinarastepina.decomposedictionary.presentation.store.DictionaryStoreFactory
import org.koin.dsl.module

val presentationModule = module {
    // Core dependencies
    single<StoreFactory> { DefaultStoreFactory() }
    
    factory { DictionaryStoreFactory(get(), get(), get()) }
    
    // Component factories using simplified approach
    factory<DictionaryComponent.Factory> {
        DefaultDictionaryComponent.Factory(get())
    }
    
    factory<TopicsComponent.Factory> {
        TopicsComponent.Factory { componentContext ->
            DefaultTopicsComponent(
                componentContext = componentContext,
                storeFactory = get(),
                topicsListComponentFactory = get(),
                phrasesComponentFactory = get(),
                searchComponentFactory = get()
            )
        }
    }
    
    // Individual component factories for internal use
    factory<TopicsListComponent.Factory> {
        TopicsListComponent.Factory { componentContext, onTopicSelected ->
            DefaultTopicsListComponent(
                componentContext = componentContext,
                storeFactory = get(),
                onTopicSelected = onTopicSelected
            )
        }
    }
    
    factory<PhrasesComponent.Factory> {
        PhrasesComponent.Factory { componentContext, topicId ->
            DefaultPhrasesComponent(
                componentContext = componentContext,
                storeFactory = get(),
                topicId = topicId
            )
        }
    }
    
    factory<SearchComponent.Factory> {
        SearchComponent.Factory { componentContext ->
            DefaultSearchComponent(
                componentContext = componentContext,
                storeFactory = get()
            )
        }
    }
    
    factory<MainComponent.Factory> {
        DefaultMainComponent.Factory(
            dictionaryComponentFactory = get(),
            topicsComponentFactory = get()
        )
    }
    
    factory<RootComponent.Factory> { 
        RootComponent.Factory { componentContext ->
            DefaultRootComponent(
                componentContext = componentContext,
                mainComponentFactory = get()
            )
        }
    }
} 