package com.deeon.submission_story_inter.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.deeon.submission_story_inter.R
import com.deeon.submission_story_inter.databinding.ActivityLoginBinding
import com.deeon.submission_story_inter.view.model.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupClickListener()
        setupObserver()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.imageLogin) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupClickListener() {
        binding.textRegisterHere.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isEmpty()) {
                binding.edLoginEmail.error = getString(R.string.input_empty)
            }
            if (password.isEmpty()) {
                binding.edLoginPassword.error = getString(R.string.input_empty)
            }
            if (binding.edLoginEmail.error.isNullOrEmpty() && binding.edLoginPassword.error.isNullOrEmpty()) {
                loginViewModel.login(email, password)
            }
        }
    }

    private fun setupObserver() {
        loginViewModel.isLoading.observe(this) {
            if (it) {
                showLoading()
                binding.btnLogin.isClickable = false
                binding.textRegisterHere.isClickable = false
            } else {
                hideLoading()
                binding.btnLogin.isClickable = true
                binding.textRegisterHere.isClickable = true
            }
        }
        loginViewModel.isError.observe(this) {
            if (it) showError(getString(R.string.network_error)) else hideError()
        }
        loginViewModel.errorMessage.observe(this) {
            if (it.isNotEmpty()) showError(getString(R.string.login_error, it)) else hideError()
        }
        loginViewModel.userSession.observe(this) { session ->
            if (session.userId.isNotEmpty()) {
                if (session.userName.isNotEmpty()) {
                    if (session.userToken.isNotEmpty()) {
                        Log.d(
                            "loginActivty::user",
                            "${session.userId} ${session.userName} ${session.userToken}"
                        )
                        Intent(this, StoryFeedActivity::class.java).apply {
                            putExtra(StoryFeedActivity.EXTRA_USER_NAME, session.userName)
                            putExtra(StoryFeedActivity.EXTRA_USER_TOKEN, session.userToken)
                        }.run {
                            startActivity(this)
                            finishAffinity()
                        }
                    }
                }
            }
        }
    }


    private fun showLoading() {
        binding.loadingBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingBar.visibility = View.INVISIBLE
    }

    private fun showError(message: String) {
        binding.tvLoginError.text = message
        binding.tvLoginError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvLoginError.visibility = View.GONE
    }
}