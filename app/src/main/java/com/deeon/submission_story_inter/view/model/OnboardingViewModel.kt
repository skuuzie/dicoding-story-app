package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deeon.submission_story_inter.data.local.UserSession
import com.deeon.submission_story_inter.data.repository.UserSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _userSession = MutableLiveData(UserSession("", "", ""))
    val userSession: LiveData<UserSession> = _userSession

    fun loadUser() {
        viewModelScope.launch {
            _userSession.value = userSessionRepository.getCurrentUser()
        }
    }
}