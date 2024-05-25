package com.deeon.submission_story_inter.data.local

data class UserSession(
    val userId: String,
    val userName: String,
    val userToken: String
)