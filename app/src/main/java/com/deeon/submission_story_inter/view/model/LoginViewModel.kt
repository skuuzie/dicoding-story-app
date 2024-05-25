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
import com.deeon.submission_story_inter.util.NetworkResult
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _userSession = MutableLiveData(UserSession("", "", ""))
    val userSession: LiveData<UserSession> = _userSession

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isError.value = false
            _isLoading.value = true
            val userSession = userSessionRepository.login(email, password)
            _isLoading.value = false
            when (userSession) {
                is NetworkResult.Success -> {
                    _isError.value = false
                    _userSession.value = userSession.data as UserSession
                }

                is NetworkResult.Error -> {
                    val errorMsg = userSession.message
                    _isError.value = true
                    if (!errorMsg.isNullOrEmpty()) _errorMessage.value = errorMsg
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val appContainer = (application as StoryApplication).appContainer

                return LoginViewModel(
                    appContainer.userSessionRepository
                ) as T
            }
        }
    }
}