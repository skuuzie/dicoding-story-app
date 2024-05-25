package com.deeon.submission_story_inter.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deeon.submission_story_inter.data.remote.StoryDetail

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryDetail>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryDetail>

    @Query("DELETE FROM story")
    suspend fun deleteAllStory()
}