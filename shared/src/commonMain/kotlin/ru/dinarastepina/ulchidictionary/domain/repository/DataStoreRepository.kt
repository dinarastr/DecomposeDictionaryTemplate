package ru.dinarastepina.ulchidictionary.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val lastSelectedLanguage: Flow<String>
    suspend fun setLastSelectedLanguage(language: String)
}