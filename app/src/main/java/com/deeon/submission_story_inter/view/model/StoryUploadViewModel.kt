package com.deeon.submission_story_inter.view.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.deeon.submission_story_inter.StoryApplication
import com.deeon.submission_story_inter.data.repository.StoryRepository
import com.deeon.submission_story_inter.util.NetworkResult
import kotlinx.coroutines.launch

class StoryUploadViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun uploadStory(
        token: String,
        storyImgUri: Uri,
        storyDescription: String,
        storyLat: Float?,
        storyLon: Float?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _isSuccess.value = false
            _isError.value = false
            val upload = storyRepository.uploadStory(
                token,
                storyImgUri,
                storyDescription,
                storyLat,
                storyLon
            )
            _isLoading.value = false
            when (upload) {
                is NetworkResult.Success -> {
                    _isError.value = false
                    _isSuccess.value = true
                }

                is NetworkResult.Error -> {
                    val errorMsg = upload.message
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

                return StoryUploadViewModel(
                    appContainer.storyRepository
                ) as T
            }
        }
    }
}