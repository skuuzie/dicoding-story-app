package com.deeon.submission_story_inter.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.data.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryFeedViewModel @Inject constructor(
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
}