package com.deeon.submission_story_inter.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.deeon.submission_story_inter.R
import com.deeon.submission_story_inter.databinding.ActivityStoryFeedBinding
import com.deeon.submission_story_inter.view.StoryUploadActivity.Companion.UPLOAD_SUCCESS_CODE
import com.deeon.submission_story_inter.view.adapter.LoadingStateAdapter
import com.deeon.submission_story_inter.view.adapter.StoryFeedAdapter
import com.deeon.submission_story_inter.view.model.StoryFeedViewModel
import kotlinx.coroutines.launch

class StoryFeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryFeedBinding
    private lateinit var adapter: StoryFeedAdapter

    private var currentUserToken: String? = null
    private var currentUserName: String? = null

    private val storyFeedModel: StoryFeedViewModel by viewModels {
        StoryFeedViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityStoryFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()

        currentUserToken = intent.getStringExtra(EXTRA_USER_TOKEN)
        currentUserName = intent.getStringExtra(EXTRA_USER_NAME)

        adapter = StoryFeedAdapter()

        setupClickListener()
        setupRecyclerView()
        setupObserver()

        storyFeedModel.fetchStoriesWithPaging(currentUserToken!!)
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.topAppBarLayout) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnAddStory) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom + 32
            }
            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.containerRv) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupClickListener() {
        binding.topAppBar.setOnMenuItemClickListener { which ->
            when (which.itemId) {
                R.id.settings -> {
                    Intent(this, SettingsActivity::class.java).run {
                        startActivity(this)
                    }
                    true
                }

                R.id.story_map -> {
                    Intent(this, StoryMapsActivity::class.java).apply {
                        putExtra(StoryMapsActivity.EXTRA_USER_TOKEN, currentUserToken)
                    }.run {
                        startActivity(this)
                    }
                    true
                }

                else -> false
            }
        }
        binding.btnAddStory.setOnClickListener {
            Intent(this, StoryUploadActivity::class.java).apply {
                putExtra(StoryUploadActivity.EXTRA_USER_TOKEN, currentUserToken)
            }.run {
                activityLauncher.launch(this)
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding) {
            rvStoryFeed.layoutManager = LinearLayoutManager(this@StoryFeedActivity)
            rvStoryFeed.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
        }
    }

    private fun setupObserver() {
        storyFeedModel.storyPagingData.observe(this) {
            lifecycleScope.launch {
                adapter.submitData(it)
            }
        }
    }

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when (it.resultCode) {
            UPLOAD_SUCCESS_CODE -> {
                storyFeedModel.fetchStoriesWithPaging(currentUserToken!!)
            }
        }
    }

    companion object {
        const val EXTRA_USER_TOKEN = "extra_user_token"
        const val EXTRA_USER_NAME = "extra_user_name"
    }
}