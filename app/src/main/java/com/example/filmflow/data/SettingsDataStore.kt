package com.example.filmflow.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsDataStore {
    private val DEFAULT_TAB_KEY = stringPreferencesKey("default_start_tab")

    suspend fun saveDefault(context: Context, tab: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_TAB_KEY] = tab
        }
    }

    fun getDefaultTab(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[DEFAULT_TAB_KEY] ?: "Discover"
        }
    }
}