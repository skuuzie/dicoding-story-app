package com.deeon.submission_story_inter.data.remote

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoryListResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("listStory")
    val result: List<StoryDetail>
)

data class StoryUploadResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "story")
@Parcelize
data class StoryDetail(
    @PrimaryKey
    @field:SerializedName("id")
    val storyId: String,
    @field:SerializedName("name")
    val storyOwnerName: String,
    @field:SerializedName("description")
    val storyDescription: String,
    @field:SerializedName("photoUrl")
    val storyImgUrl: String,
    @field:SerializedName("createdAt")
    val storyCreatedAt: String,
    @field:SerializedName("lat")
    val storyLatitude: Float?,
    @field:SerializedName("lon")
    val storyLongtitude: Float?
) : Parcelable