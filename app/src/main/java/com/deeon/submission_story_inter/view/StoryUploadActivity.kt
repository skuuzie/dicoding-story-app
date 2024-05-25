package com.deeon.submission_story_inter.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.deeon.submission_story_inter.R
import com.deeon.submission_story_inter.databinding.ActivityStoryUploadBinding
import com.deeon.submission_story_inter.view.CameraActivity.Companion.CAMERAX_INIT_FAILED
import com.deeon.submission_story_inter.view.CameraActivity.Companion.CAMERAX_RESULT
import com.deeon.submission_story_inter.view.model.StoryUploadViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StoryUploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryUploadBinding
    private val storyUploadViewModel: StoryUploadViewModel by viewModels {
        StoryUploadViewModel.Factory
    }

    private var currentImageUri: Uri? = null
    private var currentUserToken: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentUserLocation: LatLng? = null

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityStoryUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserToken = intent.getStringExtra(EXTRA_USER_TOKEN)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupEdgeToEdge()
        setupClickListener()
        setupObserver()
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
        binding.btnGallery.setOnClickListener {
            pickFromGallery()
        }
        binding.btnCamera.setOnClickListener {
            startCamera()
        }
        binding.switchLocation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!checkPermission(APPROXIMATE_LOCATION_PERMISSION) &&
                !checkPermission(PRECISE_LOCATION_PERMISSION)
            ) {
                locationPermissionRequestLauncher.launch(
                    arrayOf(
                        APPROXIMATE_LOCATION_PERMISSION,
                        PRECISE_LOCATION_PERMISSION
                    )
                )
                buttonView.isChecked = false
            } else {
                if (isChecked) getCurrentLocation()
            }
        }
        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString()
            if (currentImageUri != null && description.isNotEmpty()) {
                hideError()
                if (binding.switchLocation.isChecked && currentUserLocation != null) {
                    storyUploadViewModel.uploadStory(
                        currentUserToken!!,
                        currentImageUri!!,
                        binding.edAddDescription.text.toString(),
                        currentUserLocation!!.latitude.toFloat(),
                        currentUserLocation!!.longitude.toFloat()
                    )
                } else {
                    storyUploadViewModel.uploadStory(
                        currentUserToken!!,
                        currentImageUri!!,
                        binding.edAddDescription.text.toString(),
                        null,
                        null
                    )
                }
            } else {
                if (currentImageUri == null) {
                    showError(getString(R.string.choose_picture))
                } else {
                    if (description.isEmpty()) {
                        showError(getString(R.string.empty_description))
                    }
                }
            }
        }
    }

    private fun setupObserver() {
        storyUploadViewModel.isLoading.observe(this) {
            if (it) showLoading() else hideLoading()
        }
        storyUploadViewModel.isError.observe(this) {
            if (it) showError(getString(R.string.network_error)) else hideError()
        }
        storyUploadViewModel.errorMessage.observe(this) {
            if (it.isNotEmpty()) showError(getString(R.string.upload_error, it)) else hideError()
        }
        storyUploadViewModel.isSuccess.observe(this) {
            if (it) showSuccessDialog()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        // had to use lastlocation for emulator support
        fusedLocationClient.lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    currentUserLocation = LatLng(
                        it.latitude,
                        it.longitude
                    )
                    Log.d("LOMON", "${it.latitude} ${it.longitude}")
                } else {
                    showErrorDialog(getString(R.string.location_generic_error))
                    binding.switchLocation.isChecked = false
                }
            }
            .addOnFailureListener {
                showErrorDialog(getString(R.string.location_generic_error))
                binding.switchLocation.isChecked = false
            }

        fusedLocationClient.flushLocations()
    }

    private fun pickFromGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        if (!checkPermission(CAMERA_PERMISSION)) {
            cameraPermissionRequestLauncher.launch(CAMERA_PERMISSION)
        } else {
            launchCameraX()
        }
    }

    private fun launchCameraX() {
        launcherIntentCameraX.launch(Intent(this, CameraActivity::class.java))
    }

    private val cameraPermissionRequestLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        )
        { isGranted ->
            if (!isGranted) {
                showNeedCameraPermissionDialog()
            } else {
                launchCameraX()
            }
        }

    private val locationPermissionRequestLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            when {
                permissions.getOrDefault(PRECISE_LOCATION_PERMISSION, false) -> {
                    getCurrentLocation()
                }

                permissions.getOrDefault(APPROXIMATE_LOCATION_PERMISSION, false) -> {
                    getCurrentLocation()
                }

                else -> showNeedLocationPermissionDialog()
            }
        }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.upload))
            .setMessage(getString(R.string.upload_successful))
            .setPositiveButton(getString(R.string.close)) { _, _ ->
                setResult(UPLOAD_SUCCESS_CODE)
                finish()
            }
            .setOnCancelListener {
                setResult(UPLOAD_SUCCESS_CODE)
                finish()
            }
            .show()
    }

    private fun showNeedCameraPermissionDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.camera))
            .setMessage(getString(R.string.need_camera_permission))
            .setPositiveButton(getString(R.string.close)) { _, _ ->
                closeOptionsMenu()
            }
            .show()
    }

    private fun showNeedLocationPermissionDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.location))
            .setMessage(getString(R.string.need_location_permission))
            .setPositiveButton(getString(R.string.close)) { _, _ ->
                closeOptionsMenu()
            }
            .show()
    }

    private fun showCameraxInitializationFailureDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.camera))
            .setMessage(getString(R.string.camerax_init_error))
            .setPositiveButton(getString(R.string.close)) { _, _ ->
                finish()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(getString(R.string.close)) { _, _ ->
                finish()
            }
            .show()
    }

    private fun showImage() {
        binding.ivPhoto.setImageURI(currentImageUri)
    }

    private fun showLoading() {
        binding.loadingBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.loadingBar.visibility = View.INVISIBLE
    }

    private fun showError(message: String) {
        binding.tvErrorMessage.text = message
        binding.tvErrorMessage.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvErrorMessage.visibility = View.GONE
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            hideError()
            currentImageUri = uri
            showImage()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when (it.resultCode) {
            CAMERAX_RESULT -> {
                currentImageUri =
                    it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
                showImage()
            }

            CAMERAX_INIT_FAILED -> {
                showCameraxInitializationFailureDialog()
            }
        }
    }

    companion object {
        const val EXTRA_USER_TOKEN = "extra_user_token"
        const val UPLOAD_SUCCESS_CODE = 0xde
        private val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private val PRECISE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private val APPROXIMATE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    }
}