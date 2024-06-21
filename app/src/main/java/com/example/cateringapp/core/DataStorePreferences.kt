package com.example.cateringapp.core

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {

    val userTokenFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_TOKEN]
        }

    val userRoleFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_ROLE]
        }

    val selectedBusinessFlow: Flow<Int?> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_BUSINESS]
        }


    suspend fun updateToken(token: String) {
        val res = dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_TOKEN] = token
        }
    }

    suspend fun setRole(role: UserRolePrefs) {
        val res = dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ROLE] = role.role
        }
    }

    suspend fun setSelectedBusiness(businessId: Int) {
        val res = dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_BUSINESS] = businessId
        }
    }

    suspend fun getSelectedBusiness(): Int? {
        Log.d("UserPreferencesRepository", "getSelectedBusiness called ")
        return selectedBusinessFlow.firstOrNull()
    }
    suspend fun getToken(): String? {
        Log.d("UserPreferencesRepository", "getToken called ")
        return userTokenFlow.firstOrNull()
    }

    suspend fun getRole(): String? {
        Log.d("UserPreferencesRepository", "getRole called ")
        return userRoleFlow.firstOrNull()
    }

    suspend fun resetPrefs() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_TOKEN)
            preferences.remove(PreferencesKeys.USER_ROLE)
        }
    }


    private object PreferencesKeys {
        val USER_TOKEN = stringPreferencesKey("token")
        val USER_ROLE = stringPreferencesKey("role")
        val SELECTED_BUSINESS = intPreferencesKey("SELECTED_BUSINESS")
    }

}

enum class UserRolePrefs(val role: String) {
    USER("USER"),
    ADMIN("ADMIN")
}

private const val USER_PREFERENCES_NAME = "user_preferences"

val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)