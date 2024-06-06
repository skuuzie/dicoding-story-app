package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.data.remote.StoryListResponse
import com.deeon.submission_story_inter.data.repository.StoryRepository
import com.deeon.submission_story_inter.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryMapsViewModel @Inject constructor(
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
}