package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.deeon.submission_story_inter.StoryApplication
import com.deeon.submission_story_inter.data.local.UserSession
import com.deeon.submission_story_inter.data.repository.UserSessionRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _userSession = MutableLiveData(UserSession("", "", ""))
    val userSession: LiveData<UserSession> = _userSession

    fun loadUser() {
        viewModelScope.launch {
            _userSession.value = userSessionRepository.getCurrentUser()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val appContainer = (application as StoryApplication).appContainer

                return OnboardingViewModel(
                    appContainer.userSessionRepository
                ) as T
            }
        }
    }
}