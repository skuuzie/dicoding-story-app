package com.deeon.submission_story_inter

import android.content.Context
import androidx.room.Room
import com.deeon.submission_story_inter.data.database.StoryDatabase
import com.deeon.submission_story_inter.data.repository.StoryRepository
import com.deeon.submission_story_inter.data.repository.UserSessionRepository
import com.deeon.submission_story_inter.preferences.UserSessionPref
import com.deeon.submission_story_inter.preferences.datastore

class StoryApplicationContainer {

    private val storyDatabase = getStoryDatabase(StoryApplication.instance)
    val storyRepository = StoryRepository(StoryApplication.instance, storyDatabase)

    private val userSessionPref = UserSessionPref.getInstance(StoryApplication.instance.datastore)
    val userSessionRepository = UserSessionRepository(userSessionPref)

    companion object {
        private const val STORY_DB_NAME = "story.db"

        @Volatile
        private var STORY_DB: StoryDatabase? = null

        @JvmStatic
        fun getStoryDatabase(context: Context): StoryDatabase {
            if (STORY_DB == null) {
                synchronized(StoryDatabase::class.java) {
                    STORY_DB = Room.databaseBuilder(
                        context.applicationContext,
                        StoryDatabase::class.java,
                        STORY_DB_NAME
                    ).build()
                }
            }
            return STORY_DB as StoryDatabase
        }
    }
}