package com.deeon.submission_story_inter.view.model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deeon.submission_story_inter.data.repository.StoryRepository
import com.deeon.submission_story_inter.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryUploadViewModel @Inject constructor(
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
}