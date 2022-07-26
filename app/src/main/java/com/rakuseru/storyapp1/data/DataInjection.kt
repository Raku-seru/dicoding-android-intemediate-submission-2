package com.rakuseru.storyapp1.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rakuseru.storyapp1.data.local.AppDatabase
import com.rakuseru.storyapp1.data.network.ApiConfig
import com.rakuseru.storyapp1.data.preference.UserPreference

object DataInjection {

    fun provideStoryRepository(context: Context): StoryRepository {
        val api = ApiConfig.getApiService()
        val database = AppDatabase.getDatabase(context)
        return StoryRepository(api, database)
    }

    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepository {
        val api = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(dataStore)
        return UserRepository.getInstance(api, pref)
    }

}