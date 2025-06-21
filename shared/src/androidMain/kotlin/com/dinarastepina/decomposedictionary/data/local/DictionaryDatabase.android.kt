package com.dinarastepina.decomposedictionary.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dinarastepina.decomposedictionary.data.local.driver.DatabaseDriver
import decomposedictionary.shared.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import kotlin.io.writeBytes

private const val PREPOPULATED_DATABASE_FILE = "files/ulchi.db"
private const val CURRENT_SCHEMA_VERSION = 3 // Update this when you change the schema

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<DictionaryDataBase> {
    val appContext = ctx.applicationContext
    return Room.databaseBuilder<DictionaryDataBase>(
        context = appContext,
        name = "ulchi.db"
    ).setDriver(DatabaseDriver(appContext).createDriver())
}

@Deprecated("Use createFromAsset instead")
private suspend fun File.copyPrepopulatedDatabase() {
    parentFile?.mkdirs()
    try {
        val dbBytes = withContext(Dispatchers.IO) {
            Res.readBytes(PREPOPULATED_DATABASE_FILE)
        }
        withContext(Dispatchers.IO) {
            writeBytes(dbBytes)
            println("Successfully copied prepopulated database to: $absolutePath")
        }

    } catch (e: IOException) {
        e.printStackTrace()
        println("Error copying prepopulated database: ${e.message}")
    } catch (e: Exception) {
        e.printStackTrace()
        println("An unexpected error occurred: ${e.message}")
    }
}