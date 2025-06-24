package com.dinarastepina.decomposedictionary.presentation.components.phrasebook.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory

class DefaultSearchComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
): SearchComponent, ComponentContext by componentContext {

}