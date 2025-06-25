package ru.dinarastepina.ulchidictionary.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.dinarastepina.ulchidictionary.presentation.components.dictionary.DefaultDictionaryComponent
import ru.dinarastepina.ulchidictionary.presentation.components.dictionary.DictionaryComponent
import ru.dinarastepina.ulchidictionary.presentation.components.info.DefaultInfoComponent
import ru.dinarastepina.ulchidictionary.presentation.components.info.InfoComponent
import ru.dinarastepina.ulchidictionary.presentation.components.main.DefaultMainComponent
import ru.dinarastepina.ulchidictionary.presentation.components.main.MainComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.phrases.DefaultPhrasesComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.phrases.PhrasesComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.search.DefaultSearchComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.search.SearchComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.topics.DefaultTopicsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.topics.DefaultTopicsListComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.topics.TopicsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.phrasebook.topics.TopicsListComponent
import ru.dinarastepina.ulchidictionary.presentation.components.root.DefaultRootComponent
import ru.dinarastepina.ulchidictionary.presentation.components.root.RootComponent
import ru.dinarastepina.ulchidictionary.presentation.components.texts.DefaultTextDetailsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.texts.DefaultTextsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.texts.DefaultTextsListComponent
import ru.dinarastepina.ulchidictionary.presentation.components.texts.TextDetailsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.texts.TextsComponent
import ru.dinarastepina.ulchidictionary.presentation.components.texts.TextsListComponent
import ru.dinarastepina.ulchidictionary.presentation.store.DictionaryStoreFactory
import ru.dinarastepina.ulchidictionary.presentation.store.PhrasesStoreFactory
import ru.dinarastepina.ulchidictionary.presentation.store.SearchStoreFactory
import ru.dinarastepina.ulchidictionary.presentation.store.TextDetailsStoreFactory
import ru.dinarastepina.ulchidictionary.presentation.store.TextsListStoreFactory
import ru.dinarastepina.ulchidictionary.presentation.store.TopicsStoreFactory
import org.koin.dsl.module

val presentationModule = module {
    single<StoreFactory> { DefaultStoreFactory() }
    
    factory { DictionaryStoreFactory(get(), get(), get()) }
    factory { TopicsStoreFactory(get(), get()) }
    factory { PhrasesStoreFactory(get(), get(), get()) }
    factory { SearchStoreFactory(get(), get(), get()) }
    factory { TextsListStoreFactory(get(), get()) }
    factory { TextDetailsStoreFactory(get(), get()) }
    
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
        SearchComponent.Factory { componentContext, onNavigateBack ->
            DefaultSearchComponent(
                componentContext = componentContext,
                searchStoreFactory = get(),
                onNavigateBack = onNavigateBack
            )
        }
    }
    
    factory<TextsComponent.Factory> {
        TextsComponent.Factory { componentContext ->
            DefaultTextsComponent(
                componentContext = componentContext,
                textsListComponentFactory = get(),
                textDetailsComponentFactory = get()
            )
        }
    }
    
    factory<TextsListComponent.Factory> {
        TextsListComponent.Factory { componentContext, onTextSelected ->
            DefaultTextsListComponent(
                componentContext = componentContext,
                textsListStoreFactory = get(),
                onTextSelected = onTextSelected
            )
        }
    }
    
    factory<TextDetailsComponent.Factory> {
        TextDetailsComponent.Factory { componentContext, text, onNavigateBack ->
            DefaultTextDetailsComponent(
                componentContext = componentContext,
                textDetailsStoreFactory = get(),
                text = text,
                onNavigateBack = onNavigateBack
            )
        }
    }
    
    factory<InfoComponent.Factory> {
        InfoComponent.Factory { componentContext ->
            DefaultInfoComponent(componentContext = componentContext)
        }
    }
    
    factory<MainComponent.Factory> {
        DefaultMainComponent.Factory(
            dictionaryComponentFactory = get(),
            topicsComponentFactory = get(),
            textsComponentFactory = get(),
            infoComponentFactory = get()
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