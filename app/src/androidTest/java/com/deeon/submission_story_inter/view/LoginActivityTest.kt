package com.deeon.submission_story_inter.view

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.deeon.submission_story_inter.R
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginLogout_Success() {
        Intents.init()

        // Login
        onView(withId(R.id.ed_login_email)).perform(typeText("uiui@ui.com"))
        closeSoftKeyboard()
        onView(withId(R.id.ed_login_password)).perform(typeText("abc12345"))
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())

        // Arrived on story page and click settings
        intended(hasComponent(StoryFeedActivity::class.java.name))
        onView(withId(R.id.settings)).perform(click())

        // Arrived on settings page and logout
        intended(hasComponent(SettingsActivity::class.java.name))
        onView(withId(R.id.action_logout)).perform(click())

        // Logout dialog
        onView(withText(R.string.logout_successful)).inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withText(R.string.close)).perform(click())

        // Logout successful
        intended(hasComponent(OnboardingActivity::class.java.name))
    }
}