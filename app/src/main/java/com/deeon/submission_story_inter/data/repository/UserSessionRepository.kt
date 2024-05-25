package com.deeon.submission_story_inter.data.repository

import com.deeon.submission_story_inter.data.local.UserSession
import com.deeon.submission_story_inter.data.remote.LoginResponse
import com.deeon.submission_story_inter.data.remote.retrofit.DicodingApiConfig
import com.deeon.submission_story_inter.preferences.UserSessionPref
import com.deeon.submission_story_inter.util.NetworkResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.net.UnknownHostException

class UserSessionRepository(private val session: UserSessionPref) {

    suspend fun getCurrentUser(): UserSession {
        return UserSession(
            session.userId.first(),
            session.userName.first(),
            session.userToken.first()
        )
    }

    private suspend fun saveUserSession(userId: String, userName: String, userToken: String): Int {
        session.saveUserSession(userId, userName, userToken)
        return 0
    }

    suspend fun deleteUserSession(): Int {
        session.deleteUserSession()
        return 0
    }

    suspend fun login(email: String, password: String): NetworkResult<Any> {
        return try {
            val response = DicodingApiConfig.getApiService(null).login(email, password)
            val userId = response.loginResult.userId
            val userName = response.loginResult.userName
            val userToken = response.loginResult.userToken
            saveUserSession(userId, userName, userToken)
            NetworkResult.Success(
                UserSession(
                    userId, userName, userToken
                )
            )
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            NetworkResult.Error(errorResponse.message)
        } catch (e: UnknownHostException) {
            NetworkResult.Error(null)
        }
    }

    suspend fun register(name: String, email: String, password: String): NetworkResult<Any> {
        return try {
            DicodingApiConfig.getApiService(null).register(name, email, password)
            NetworkResult.Success(0)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            NetworkResult.Error(errorResponse.message)
        } catch (e: UnknownHostException) {
            NetworkResult.Error(null)
        }
    }

    companion object {
        @Volatile
        private var instance: UserSessionRepository? = null
        fun getInstance(pref: UserSessionPref) =
            instance ?: synchronized(this) {
                instance ?: UserSessionRepository(pref)
            }.also { instance = it }
    }
}