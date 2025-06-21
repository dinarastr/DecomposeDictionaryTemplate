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
import platform.Foundation.NSURL


private const val DATABASE_NAME: String = "nanay_dictionary.db"
private const val PREPOPULATED_DATABASE_FILE = "files/nanay_to_russian"

@OptIn(ExperimentalForeignApi::class)
fun getDatabaseBuilder(): RoomDatabase.Builder<DictionaryDatabase> {
    val fileManager = NSFileManager.defaultManager
    val documentsDirectory = fileManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val dbFilePath = documentsDirectory?.path + "/ulchi.db"
    return Room.databaseBuilder<DictionaryDatabase>(
        name = dbFilePath
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
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

