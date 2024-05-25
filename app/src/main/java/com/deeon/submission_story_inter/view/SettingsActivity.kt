package com.deeon.submission_story_inter.view

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.deeon.submission_story_inter.R
import com.deeon.submission_story_inter.databinding.ActivitySettingsBinding
import com.deeon.submission_story_inter.view.model.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.actionLogout.setOnClickListener {
            settingsViewModel.logout()
            showLogoutDialog()
        }
        binding.actionChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }
        binding.topAppBar.setNavigationOnClickListener {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun logoutFromAccount() {
        Intent(this, OnboardingActivity::class.java).run {
            startActivity(this)
            finishAffinity()
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_successful))
            .setPositiveButton(getString(R.string.close)) { _, _ ->
                closeOptionsMenu()
                logoutFromAccount()
            }
            .setOnCancelListener {
                closeOptionsMenu()
                logoutFromAccount()
            }
            .show()
    }

    private fun showLanguageDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.change_language))
            .setItems(arrayOf("English", "Bahasa")) { _, which ->
                when (which) {
                    0 -> AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags("en-us")
                    )

                    1 -> AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags("id")
                    )
                }
            }
            .show()
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
}