package com.rakuseru.storyapp1.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.rakuseru.storyapp1.data.local.AppDatabase
import com.rakuseru.storyapp1.data.local.StoryEntity
import com.rakuseru.storyapp1.data.network.ApiService
import com.rakuseru.storyapp1.data.remote.StoryRemoteMediator
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStory(token: String): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(token, apiService, database),
            pagingSourceFactory = {
                database.storiesDao().getAllStories()
            }).liveData
    }

    suspend fun getAllStoryWithLocation(token: String) =
        apiService.getStories("Bearer $token", size = 20, location = 1)

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = apiService.uploadImage(file, description,"Bearer $token", lat, lon)

    companion object {
        private var INSTANCE: StoryRepository? = null

        fun getInstance(apiService: ApiService, database: AppDatabase): StoryRepository {
            return INSTANCE ?: synchronized(this) {
                StoryRepository(apiService, database)
                    .also { INSTANCE = it }
            }
        }
    }

}