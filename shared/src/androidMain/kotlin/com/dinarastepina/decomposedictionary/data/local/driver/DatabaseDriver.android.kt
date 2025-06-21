package com.dinarastepina.decomposedictionary.data.local.driver

import android.content.Context
import androidx.room.Room
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.dinarastepina.decomposedictionary.data.local.DictionaryDataBase

actual class DatabaseDriver(private val context: Context) {
    actual fun createDriver(): SQLiteDriver {
        val db = Room.databaseBuilder(
            context,
            DictionaryDataBase::class.java,
            "ulchi.db"
        )
            .fallbackToDestructiveMigration(true)
            .createFromAsset("files/ulchi.db")
            .build()
            
        val writableDb = db.openHelper.writableDatabase
        writableDb.close()
        
        return AndroidSQLiteDriver()
    }
} 