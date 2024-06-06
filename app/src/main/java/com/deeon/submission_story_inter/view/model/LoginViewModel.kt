package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deeon.submission_story_inter.data.local.UserSession
import com.deeon.submission_story_inter.data.repository.UserSessionRepository
import com.deeon.submission_story_inter.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
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
}