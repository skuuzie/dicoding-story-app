package com.deeon.submission_story_inter.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class UserSessionPref private constructor(private val dataStore: DataStore<Preferences>) {
    private val USER_ID = stringPreferencesKey("user_id")
    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_TOKEN = stringPreferencesKey("user_token")

    val userId: Flow<String> = this.dataStore.data
        .map { session ->
            session[USER_ID] ?: ""
        }

    val userName: Flow<String> = this.dataStore.data
        .map { session ->
            session[USER_NAME] ?: ""
        }

    val userToken: Flow<String> = this.dataStore.data
        .map { session ->
            session[USER_TOKEN] ?: ""
        }

    suspend fun saveUserSession(userId: String, userName: String, userToken: String) {
        this.dataStore.edit { session ->
            session[USER_ID] = userId
            session[USER_NAME] = userName
            session[USER_TOKEN] = userToken
        }
    }

    suspend fun deleteUserSession() {
        this.dataStore.edit { session ->
            session[USER_ID] = ""
            session[USER_NAME] = ""
            session[USER_TOKEN] = ""
        }
    }

    companion object {
        private var INSTANCE: UserSessionPref? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserSessionPref {
            return INSTANCE ?: synchronized(this) {
                val instance = UserSessionPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}