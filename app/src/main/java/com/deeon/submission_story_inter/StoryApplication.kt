package com.deeon.submission_story_inter

import android.app.Application

class StoryApplication : Application() {
    lateinit var appContainer: StoryApplicationContainer

    companion object {
        lateinit var instance: StoryApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = StoryApplicationContainer()
    }
}