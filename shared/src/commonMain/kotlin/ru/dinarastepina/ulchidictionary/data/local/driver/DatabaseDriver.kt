package ru.dinarastepina.ulchidictionary.data.local.driver

import androidx.sqlite.SQLiteDriver

expect class DatabaseDriver {
    fun createDriver(): SQLiteDriver
} 