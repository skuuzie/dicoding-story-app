package com.deeon.submission_story_inter.data.repository

import android.content.Context
import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.deeon.submission_story_inter.data.database.StoryDatabase
import com.deeon.submission_story_inter.data.remote.LoginResponse
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.data.remote.retrofit.DicodingApiConfig
import com.deeon.submission_story_inter.util.NetworkResult
import com.deeon.submission_story_inter.util.Utils.reduceFileImage
import com.deeon.submission_story_inter.util.Utils.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.UnknownHostException


class StoryRepository(
    private val context: Context,
    private val storyDatabase: StoryDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun fetchStoriesWithPaging(
        token: String
    ): Flow<PagingData<StoryDetail>> {
        val apiService = DicodingApiConfig.getApiService(token)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).flow
    }

    suspend fun fetchStories(
        token: String,
        page: Int?,
        size: Int?,
        location: Int?
    ): NetworkResult<Any>? {
        return try {
            val stories = DicodingApiConfig.getApiService(token).getAllStories(page, size, location)
            NetworkResult.Success(stories)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            NetworkResult.Error(errorResponse.message)
        } catch (e: UnknownHostException) {
            NetworkResult.Error(null)
        }
    }

    suspend fun uploadStory(
        token: String, uri: Uri, description: String,
        lat: Float?, lon: Float?
    ): NetworkResult<String> {
        val image = uriToFile(uri, context).reduceFileImage()

        val requestDescription = description.toRequestBody("text/plain".toMediaType())

        val imageBody = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            image.asRequestBody("image/jpeg".toMediaType())
        )

        return try {
            val res = DicodingApiConfig.getApiService(token).uploadStory(
                imageBody, requestDescription, lat, lon
            )
            NetworkResult.Success(res.message)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            NetworkResult.Error(errorResponse.message)
        } catch (e: UnknownHostException) {
            NetworkResult.Error(null)
        }
    }

}