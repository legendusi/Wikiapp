package com.example.wikiapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_preferences")

class DataStoreHelper(private val context: Context) {
    private val HISTORY_KEY = stringSetPreferencesKey("history")

    val historyFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[HISTORY_KEY] ?: emptySet()
    }

    suspend fun saveHistory(history: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_KEY] = history
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_KEY] = emptySet()
        }
    }
}