package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.deeon.submission_story_inter.StoryApplication
import com.deeon.submission_story_inter.data.repository.UserSessionRepository
import com.deeon.submission_story_inter.util.NetworkResult
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userSessionRepository: UserSessionRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isError.value = false
            _isLoading.value = true
            val register = userSessionRepository.register(name, email, password)
            _isLoading.value = false
            when (register) {
                is NetworkResult.Success -> {
                    _isError.value = false
                    _isSuccess.value = true
                }

                is NetworkResult.Error -> {
                    val errorMsg = register.message
                    _isError.value = true
                    if (!errorMsg.isNullOrEmpty()) _errorMessage.value = errorMsg
                }

                else -> {}
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

                return RegisterViewModel(
                    appContainer.userSessionRepository
                ) as T
            }
        }
    }
}