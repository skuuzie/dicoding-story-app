package com.deeon.submission_story_inter.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.deeon.submission_story_inter.R
import com.deeon.submission_story_inter.databinding.ActivityRegisterBinding
import com.deeon.submission_story_inter.view.model.RegisterViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupClickListener()
        setupObserver()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.imageRegister) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupClickListener() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            if (name.isEmpty()) {
                binding.edRegisterName.error = getString(R.string.input_empty)
            }
            if (email.isEmpty()) {
                binding.edRegisterEmail.error = getString(R.string.input_empty)
            }
            if (password.isEmpty()) {
                binding.edRegisterPassword.error = getString(R.string.input_empty)
            }
            if (binding.edRegisterName.error.isNullOrEmpty() &&
                binding.edRegisterEmail.error.isNullOrEmpty() &&
                binding.edRegisterPassword.error.isNullOrEmpty()
            ) {
                registerViewModel.register(name, email, password)
            }
        }
    }

    private fun setupObserver() {
        registerViewModel.isLoading.observe(this) {
            if (it) {
                showLoading()
                binding.btnRegister.isClickable = false
            } else {
                hideLoading()
                binding.btnRegister.isClickable = true
            }
        }
        registerViewModel.isError.observe(this) {
            if (it) showError(getString(R.string.network_error)) else hideError()
        }
        registerViewModel.errorMessage.observe(this) {
            if (it.isNotEmpty()) showError(getString(R.string.register_error, it)) else hideError()
        }
        registerViewModel.isSuccess.observe(this) {
            if (it) showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.register))
            .setMessage(getString(R.string.register_successful))
            .setPositiveButton(getString(R.string.close)) { _, _ ->
                finish()
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }

    private fun showLoading() {
        binding.loadingBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingBar.visibility = View.INVISIBLE
    }

    private fun showError(message: String) {
        binding.tvRegisterError.text = message
        binding.tvRegisterError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvRegisterError.visibility = View.GONE
    }
}