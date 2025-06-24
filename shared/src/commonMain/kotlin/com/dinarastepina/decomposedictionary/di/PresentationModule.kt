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
import com.dinarastepina.decomposedictionary.presentation.store.PhrasesStoreFactory
import com.dinarastepina.decomposedictionary.presentation.store.TopicsStoreFactory
import org.koin.dsl.module

val presentationModule = module {
    single<StoreFactory> { DefaultStoreFactory() }
    
    factory { DictionaryStoreFactory(get(), get(), get()) }
    factory { TopicsStoreFactory(get(), get()) }
    factory { PhrasesStoreFactory(get(), get(), get()) }
    
    factory<DictionaryComponent.Factory> {
        DefaultDictionaryComponent.Factory(get())
    }
    
    factory<TopicsComponent.Factory> {
        TopicsComponent.Factory { componentContext ->
            DefaultTopicsComponent(
                componentContext = componentContext,
                topicsListComponentFactory = get(),
                phrasesComponentFactory = get(),
                searchComponentFactory = get()
            )
        }
    }
    
    factory<TopicsListComponent.Factory> {
        TopicsListComponent.Factory { componentContext, onTopicSelected, onSearchBarClicked ->
            DefaultTopicsListComponent(
                componentContext = componentContext,
                topicsStoreFactory = get(),
                onTopicSelected = onTopicSelected,
                onSearchBarClicked = onSearchBarClicked
            )
        }
    }
    
    factory<PhrasesComponent.Factory> {
        PhrasesComponent.Factory { componentContext, topic, onNavigateBack ->
            DefaultPhrasesComponent(
                componentContext = componentContext,
                topic = topic,
                phrasesStoreFactory = get(),
                onNavigateBack = onNavigateBack
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