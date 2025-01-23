package com.example.wikiapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// data/SavedPage.kt
@Entity(tableName = "saved_pages")
data class SavedPage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,  // Must be Long for autoGenerate
    val title: String,
    val url: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val htmlContent: String
)