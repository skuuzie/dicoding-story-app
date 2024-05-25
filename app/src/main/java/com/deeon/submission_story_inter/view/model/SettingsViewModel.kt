package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.deeon.submission_story_inter.StoryApplication
import com.deeon.submission_story_inter.data.repository.UserSessionRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            userSessionRepository.deleteUserSession()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val appContainer = (application as StoryApplication).appContainer

                return SettingsViewModel(
                    appContainer.userSessionRepository
                ) as T
            }
        }
    }
}