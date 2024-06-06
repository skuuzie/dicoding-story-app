package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deeon.submission_story_inter.data.repository.UserSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            userSessionRepository.deleteUserSession()
        }
    }
}