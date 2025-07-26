package com.example.spacemeta.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("controle_prefs")

class DataStoreManager(private val context: Context) {
    fun getValor(key: String): Flow<String> {
        val dataKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[dataKey] ?: ""
        }
    }

    suspend fun salvarValor(key: String, value: String) {
        val dataKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[dataKey] = value
        }
    }

    suspend fun limparTudo() {
        context.dataStore.edit { it.clear() }
    }

}