package com.dinarastepina.decomposedictionary.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dinarastepina.decomposedictionary.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {
    private object PreferencesKeys {
        val LAST_SELECTED_LANGUAGE = stringPreferencesKey("last_selected_language")
    }

    override val lastSelectedLanguage: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_SELECTED_LANGUAGE] ?: "русский"
    }

    override suspend fun setLastSelectedLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SELECTED_LANGUAGE] = language
        }
    }
} 