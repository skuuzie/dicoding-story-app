package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.deeon.submission_story_inter.StoryApplication
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.data.remote.StoryListResponse
import com.deeon.submission_story_inter.data.repository.StoryRepository
import com.deeon.submission_story_inter.util.NetworkResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoryFeedViewModel(
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _storyPagingData = MutableLiveData<PagingData<StoryDetail>>()
    val storyPagingData: LiveData<PagingData<StoryDetail>> = _storyPagingData

    fun fetchStoriesWithPaging(token: String) {
        viewModelScope.launch {
            storyRepository.fetchStoriesWithPaging(token).cachedIn(viewModelScope).collectLatest {
                _storyPagingData.value = it
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

                return StoryFeedViewModel(
                    appContainer.storyRepository
                ) as T
            }
        }
    }
}