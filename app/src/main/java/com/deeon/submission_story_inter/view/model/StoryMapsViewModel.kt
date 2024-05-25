package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.deeon.submission_story_inter.StoryApplication
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.data.remote.StoryListResponse
import com.deeon.submission_story_inter.data.repository.StoryRepository
import com.deeon.submission_story_inter.util.NetworkResult
import kotlinx.coroutines.launch

class StoryMapsViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _storyList = MutableLiveData<List<StoryDetail>>()
    val storyList: LiveData<List<StoryDetail>> = _storyList

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchStoriesWithLocation(token: String) {
        viewModelScope.launch {
            _isError.value = false
            _isLoading.value = true
            val stories = storyRepository.fetchStories(token, null, null, 1)
            _isLoading.value = false
            when (stories) {
                is NetworkResult.Success -> {
                    _isError.value = false
                    _storyList.value = (stories.data as StoryListResponse).result
                }

                is NetworkResult.Error -> {
                    val errorMsg = stories.message
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

                return StoryMapsViewModel(
                    appContainer.storyRepository
                ) as T
            }
        }
    }
}