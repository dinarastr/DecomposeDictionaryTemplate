package com.dinarastepina.decomposedictionary.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import com.dinarastepina.decomposedictionary.data.local.driver.DatabaseDriver
import decomposedictionary.shared.generated.resources.Res
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import kotlin.toULong


private const val DATABASE_NAME: String = "nanay_dictionary.db"
private const val PREPOPULATED_DATABASE_FILE = "files/nanay_to_russian"

@OptIn(ExperimentalForeignApi::class)
 fun getDatabaseBuilder(): RoomDatabase.Builder<DictionaryDataBase> {
    val fileManager = NSFileManager.defaultManager
    val documentsDirectory = fileManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val dbFilePath = documentsDirectory?.path + "/$DATABASE_NAME"
    val dbFileExists = fileManager.fileExistsAtPath(dbFilePath)

    if (!dbFileExists) {
        val dbBytes = runBlocking {
            try {
                Res.readBytes(PREPOPULATED_DATABASE_FILE)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        if (dbBytes != null) {
            val nsData = dbBytes.toNSData()
            if (nsData != null) {
                try {
                    val success = fileManager.createFileAtPath(
                        dbFilePath,
                        nsData,
                        null
                    )
                    if (success) {
                        println("Successfully copied prepopulated database to: $dbFilePath")
                    } else {
                        println("Failed to copy prepopulated database to: $dbFilePath")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Error copying prepopulated database: ${e.message}")
                }
            } else {
                println("Error converting ByteArray to NSData.")
            }
        } else {
            println("Error reading prepopulated database bytes from shared resources.")
        }
    }
    return Room.databaseBuilder<DictionaryDataBase>(
        name = dbFilePath
    ).setDriver(DatabaseDriver().createDriver())
        .fallbackToDestructiveMigration(true)
}

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData? {
    if (isEmpty()) return NSData()
    return memScoped {
        this@toNSData.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
        }
    }
}