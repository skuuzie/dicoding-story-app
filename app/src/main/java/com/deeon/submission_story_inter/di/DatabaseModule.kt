package com.deeon.submission_story_inter.di

import android.content.Context
import androidx.room.Room
import com.deeon.submission_story_inter.data.database.AppDatabase
import com.deeon.submission_story_inter.data.database.RemoteKeysDao
import com.deeon.submission_story_inter.data.database.StoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    private const val STORY_DB_NAME = "story.db"

    @Provides
    fun provideStoryDao(database: AppDatabase): StoryDao {
        return database.storyDao()
    }

    @Provides
    fun provideRemoteKeysDao(database: AppDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            AppDatabase::class.java,
            STORY_DB_NAME
        ).build()
    }
}