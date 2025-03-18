package com.hisuperaman.cartit.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val Context.dataStore by preferencesDataStore("user_prefs")

    private val KEY_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")

    suspend fun saveUserSession(context: Context, email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOGGED_IN] = true
            preferences[KEY_USER_EMAIL] = email
        }
    }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOGGED_IN] = false
            preferences.remove(KEY_USER_EMAIL)
        }
    }

    fun getUserSession(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_LOGGED_IN] ?: false
        }
    }

    fun getUserEmail(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_USER_EMAIL]
        }
    }
}