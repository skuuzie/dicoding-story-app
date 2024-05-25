package com.deeon.submission_story_inter.utils

import com.deeon.submission_story_inter.data.remote.StoryDetail

object DummyData {
    fun generateDummyStories(size: Int): List<StoryDetail> {
        val storyList = ArrayList<StoryDetail>()
        for (i in 0..<size) {
            storyList.add(
                StoryDetail(
                    "$i",
                    "owner $i",
                    "desc $i",
                    "url $i",
                    "created_at $i",
                    null,
                    null
                )
            )
        }
        return storyList
    }
}