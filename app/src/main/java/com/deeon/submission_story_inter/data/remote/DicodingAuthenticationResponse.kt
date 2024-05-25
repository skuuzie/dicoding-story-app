package com.deeon.submission_story_inter.data.remote

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("loginResult")
    val loginResult: LoginResult
)

data class RegisterResponse(
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String,
)

data class LoginResult(
    @field:SerializedName("userId")
    val userId: String,
    @field:SerializedName("name")
    val userName: String,
    @field:SerializedName("token")
    val userToken: String
)