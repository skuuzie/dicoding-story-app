package com.deeon.submission_story_inter.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.deeon.submission_story_inter.databinding.ActivityOnboardingBinding
import com.deeon.submission_story_inter.view.model.OnboardingViewModel

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val onboardingViewModel: OnboardingViewModel by viewModels {
        OnboardingViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingViewModel.loadUser()

        setupUserSession()
        setupEdgeToEdge()
        setupClickListener()
    }

    private fun setupUserSession() {
        onboardingViewModel.userSession.observe(this) { session ->
            if (session.userId.isNotEmpty()) {
                if (session.userName.isNotEmpty()) {
                    if (session.userToken.isNotEmpty()) {
                        Log.d(
                            "setupUserSession",
                            "${session.userId} ${session.userName} ${session.userToken}"
                        )
                        Intent(this, StoryFeedActivity::class.java).apply {
                            putExtra(StoryFeedActivity.EXTRA_USER_NAME, session.userName)
                            putExtra(StoryFeedActivity.EXTRA_USER_TOKEN, session.userToken)
                        }.run {
                            startActivity(this)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setupEdgeToEdge() {

        ViewCompat.setOnApplyWindowInsetsListener(binding.imageOnboarding) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupClickListener() {
        binding.btnExplore.setOnClickListener {
            Intent(this, LoginActivity::class.java).run {
                startActivity(this)
            }
        }
    }
}