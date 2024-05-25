package com.deeon.submission_story_inter.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deeon.submission_story_inter.data.remote.StoryDetail

@Database(
    entities = [StoryDetail::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}