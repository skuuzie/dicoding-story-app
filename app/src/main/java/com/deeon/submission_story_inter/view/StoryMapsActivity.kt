package com.deeon.submission_story_inter.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.deeon.submission_story_inter.R
import com.deeon.submission_story_inter.data.remote.StoryDetail
import com.deeon.submission_story_inter.databinding.ActivityStoryMapsBinding
import com.deeon.submission_story_inter.view.model.StoryMapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityStoryMapsBinding

    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    private var currentUserToken: String? = null

    private val storyMapsModel: StoryMapsViewModel by viewModels {
        StoryMapsViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()

        currentUserToken = intent.getStringExtra(StoryFeedActivity.EXTRA_USER_TOKEN)

        setupClickListener()
        setupObserver()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_styles))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        storyMapsModel.fetchStoriesWithLocation(currentUserToken!!)

        storyMapsModel.storyList.observe(this) {
            setStoryMarkers(it)
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.topAppBarLayout) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map)) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setStoryMarkers(stories: List<StoryDetail>) {
        stories.forEach {
            val pos = LatLng(it.storyLatitude!!.toDouble(), it.storyLongtitude!!.toDouble())

            mMap.addMarker(
                MarkerOptions()
                    .position(pos)
                    .title(it.storyOwnerName)
                    .snippet(it.storyDescription)
            )
            boundsBuilder.include(pos)
        }

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                boundsBuilder.build(),
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setupClickListener() {
        binding.topAppBar.setNavigationOnClickListener {
            super.onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupObserver() {
        storyMapsModel.isLoading.observe(this) {
            if (it) showLoading() else hideLoading()
        }
        storyMapsModel.isError.observe(this) {
            if (it) {
                showToast(getString(R.string.network_error))
            }
        }
        storyMapsModel.errorMessage.observe(this) {
            if (it.isNotEmpty()) showToast(it)
        }
    }

    private fun showLoading() {
        binding.loadingBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingBar.visibility = View.GONE
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_USER_TOKEN = "extra_user_token"
    }
}