package com.deeon.submission_story_inter.view

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    private var story: StoryDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()

        story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_STORY_DETAIL, StoryDetail::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_STORY_DETAIL)
        }

        setupClickListener()
        setupData()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.topAppBarLayout) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupClickListener() {
        binding.topAppBar.setNavigationOnClickListener {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupData() {
        with(binding) {
            tvDetailDescription.text = story?.storyDescription
            tvDetailName.text = story?.storyOwnerName
            Glide.with(binding.root)
                .load(story?.storyImgUrl)
                .into(binding.ivDetailPhoto)
        }
    }

    companion object {
        const val EXTRA_STORY_DETAIL = "extra_story_detail"
    }
}