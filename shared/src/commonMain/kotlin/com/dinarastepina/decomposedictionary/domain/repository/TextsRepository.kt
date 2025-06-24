package com.dinarastepina.decomposedictionary.domain.repository

import com.dinarastepina.decomposedictionary.domain.model.Text

interface TextsRepository {
    suspend fun fetchAllTexts(): List<Text>
}