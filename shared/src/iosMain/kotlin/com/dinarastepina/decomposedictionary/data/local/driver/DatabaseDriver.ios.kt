package com.dinarastepina.decomposedictionary.data.local.driver

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class DatabaseDriver {
    actual fun createDriver(): SQLiteDriver {
        return BundledSQLiteDriver()
    }
} 