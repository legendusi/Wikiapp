// data/AppDatabase.kt
package com.example.wikiapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SavedPage::class],
    version = 2,  // Incremented version for the changes
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedPageDao(): SavedPageDao
}