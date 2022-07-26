package com.rakuseru.storyapp1.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoriesDao {

    @Query("SELECT * FROM app_stories")
    fun getAllStories(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(vararg storyEntity: StoryEntity)

    @Query("DELETE FROM app_stories")
    fun deleteAll()
}