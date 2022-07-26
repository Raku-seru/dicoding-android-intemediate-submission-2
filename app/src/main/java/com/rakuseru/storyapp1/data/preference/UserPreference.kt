package com.rakuseru.storyapp1.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getLoginState(): Flow<Boolean> {
        return dataStore.data.map { pref ->
            pref[IS_LOGIN] ?: false
        }
    }

    suspend fun saveUser(token: String){
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    fun getUser(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    suspend fun deleteUser(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val IS_LOGIN = booleanPreferencesKey("is_login")
        private val TOKEN = stringPreferencesKey("token")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}