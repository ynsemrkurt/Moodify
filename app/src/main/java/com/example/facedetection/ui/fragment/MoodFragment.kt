package com.example.facedetection.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.facedetection.R
import com.example.facedetection.databinding.FragmentMoodBinding
import com.example.facedetection.ui.utils.AdManager
import com.example.facedetection.ui.utils.Mood
import com.example.facedetection.ui.utils.Permission.DATA
import com.example.facedetection.ui.utils.Permission.FRONT_CAMERA
import com.example.facedetection.ui.utils.Permission.PACKAGE
import com.example.facedetection.ui.utils.Spotify
import com.example.facedetection.ui.utils.getParcelable
import com.example.facedetection.ui.utils.showSnackbar
import com.example.facedetection.ui.viewModel.MoodViewModel

class MoodFragment : Fragment() {

    private lateinit var binding: FragmentMoodBinding
    private val moodViewModel: MoodViewModel by viewModels()
    private var token: String? = null
    private lateinit var adManager: AdManager

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                moodViewModel.analyzeSelectedImage(requireContext(), it)
            } ?: run {
                binding.moodTextView.text = getString(R.string.no_photo_selected)
            }
        }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                binding.root.showSnackbar(R.string.camera_permission_denied, ::openAppSettings)
            }
        }

    private val requestStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openGallery()
            } else {
                binding.root.showSnackbar(R.string.gallery_permission_denied, ::openAppSettings)
            }
        }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts(PACKAGE, requireActivity().packageName, null)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        token = arguments?.getString(Spotify.TOKEN_KEY)
        binding = FragmentMoodBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adManager = AdManager(requireActivity())
        adManager.loadRewardedAd()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.imageBtnSelectPhoto.setOnClickListener {
            if (checkSelfPermission(
                    requireContext(), android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                requestCameraPermission.launch(android.Manifest.permission.CAMERA)
            }
        }

        binding.selectPhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                adManager.showAdIfAvailable {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            } else {
                if (checkSelfPermission(
                        requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery()
                } else {
                    requestStoragePermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }


    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(FRONT_CAMERA, 1)
        }
        resultLauncher.launch(takePictureIntent)
    }

    private fun openGallery() {
        adManager.showAdIfAvailable {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intent)
        }
    }

    private fun observeViewModel() {
        moodViewModel.mood.observe(viewLifecycleOwner) { mood ->
            navigateToListFragment(mood)
        }

        moodViewModel.error.observe(viewLifecycleOwner) { error ->
            binding.moodTextView.text = error
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleActivityResult(result.data)
            }
        }

    private fun handleActivityResult(data: Intent?) {
        if (data != null) {
            val imageBitmap: Bitmap? = data.getParcelable(DATA, Bitmap::class.java)
            if (imageBitmap != null) {
                moodViewModel.analyzeSelectedImageFromBitmap(imageBitmap)
            } else {
                val selectedImageUri = data.data
                selectedImageUri?.let {
                    moodViewModel.analyzeSelectedImage(requireContext(), it)
                }
            }
        }
    }

    private fun navigateToListFragment(mood: String) {
        val listFragment = ListFragment().apply {
            arguments = Bundle().apply {
                putString(Mood.MOOD, mood)
                putString(Spotify.TOKEN_KEY, token)
            }
        }

        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, listFragment)
            .addToBackStack(null).commit()
    }
}