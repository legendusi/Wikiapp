// data/SavedPageDao.kt
package com.example.wikiapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPageDao {
    @Query("SELECT * FROM saved_pages ORDER BY id DESC")
    fun getAllSavedPages(): Flow<List<SavedPage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(page: SavedPage): Long

    @Delete
    suspend fun delete(page: SavedPage)

    // Optional: Add if you need to get a single page by ID
    @Query("SELECT * FROM saved_pages WHERE id = :id")
    fun getPageById(id: Long): Flow<SavedPage?>
}