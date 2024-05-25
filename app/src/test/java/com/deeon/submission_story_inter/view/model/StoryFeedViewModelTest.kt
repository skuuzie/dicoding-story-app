package com.deeon.submission_story_inter.view.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.data.repository.StoryRepository
import com.deeon.submission_story_inter.utils.DummyData
import com.deeon.submission_story_inter.utils.LiveDataTestUtil.getOrAwaitValue
import com.deeon.submission_story_inter.utils.MainDispatcherRule
import com.deeon.submission_story_inter.view.adapter.StoryFeedAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StoryFeedViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when fetchStoriesWithPaging should not null and return data size as expected`() = runTest {
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTVva3NZWERZeFp5d2xLVUoiLCJpYXQiOjE3MTU1MDgwODV9.no4lKhPayq4Hh8Cak20mTCuJwKAM6f0ZI23THmmgpfE"

        val dummyStories = DummyData.generateDummyStories(100)
        val data = StoryPagingSource.snapshot(dummyStories)
        `when`(storyRepository.fetchStoriesWithPaging(token)).thenReturn(flowOf(data))

        val storyFeedViewModel = StoryFeedViewModel(storyRepository)
        storyFeedViewModel.fetchStoriesWithPaging(token)
        val actualStories = storyFeedViewModel.storyPagingData.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryFeedAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(actualStories)
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when fetchStoriesWithPaging empty should return no data`() = runTest {
        val token =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLTVva3NZWERZeFp5d2xLVUoiLCJpYXQiOjE3MTU1MDgwODV9.no4lKhPayq4Hh8Cak20mTCuJwKAM6f0ZI23THmmgpfE"

        val dummyStories: PagingData<StoryDetail> = PagingData.from(emptyList())
        `when`(storyRepository.fetchStoriesWithPaging(token)).thenReturn(flowOf(dummyStories))

        val storyFeedViewModel = StoryFeedViewModel(storyRepository)
        storyFeedViewModel.fetchStoriesWithPaging(token)
        val actualStories = storyFeedViewModel.storyPagingData.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryFeedAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<StoryDetail>>>() {
    companion object {
        fun snapshot(items: List<StoryDetail>): PagingData<StoryDetail> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryDetail>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryDetail>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}