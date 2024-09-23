package com.example.facedetection.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.facedetection.R
import com.example.facedetection.databinding.FragmentMoodBinding
import com.example.facedetection.ui.utils.Mood
import com.example.facedetection.ui.utils.PermissionManager
import com.example.facedetection.ui.utils.Spotify
import com.example.facedetection.ui.viewModel.MoodViewModel

class MoodFragment : Fragment() {

    private lateinit var binding: FragmentMoodBinding
    private val moodViewModel: MoodViewModel by viewModels()
    private var loadingDialog: AlertDialog? = null
    private var token: String? = null
    private lateinit var permissionManager: PermissionManager
    private val REQUEST_IMAGE_CAPTURE = 101

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                binding.imageView.setImageURI(it)
                showLoadingDialog()
                moodViewModel.analyzeSelectedImage(requireContext(), it)
            } ?: run {
                binding.moodTextView.text = getString(R.string.no_photo_selected)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        token = arguments?.getString(Spotify.TOKEN_KEY)
        binding = FragmentMoodBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // PermissionManager'ı başlatıyoruz
        permissionManager = PermissionManager(requireActivity())

        val openCameraButton: ImageButton = view.findViewById(R.id.imageBtnSelectPhoto)
        openCameraButton.setOnClickListener {
            if (permissionManager.checkCameraPermission()) {
                openCamera()
            } else {
                permissionManager.requestCameraPermission()
            }
        }

        binding.selectPhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                checkAndRequestPermissions()
            }
        }

        moodViewModel.mood.observe(viewLifecycleOwner) { mood ->
            if (mood.startsWith("Error")) {
                binding.moodTextView.text = mood
            } else {
                navigateToListFragment(mood)
            }
            dismissLoadingDialog()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Fotoğrafı ImageView'de gösterelim
            binding.imageView.setImageBitmap(imageBitmap)

            // Fotoğrafı işleme
            showLoadingDialog()
            moodViewModel.analyzeSelectedImageFromBitmap(imageBitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionManager.handlePermissionsResult(
            requestCode,
            grantResults,
            onPermissionGranted = { openCamera() },
            onPermissionDenied = {
                Toast.makeText(context, "Kamera izni reddedildi", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 102)
    }

    private fun showLoadingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        loadingDialog?.show()

        moodViewModel.executeWithDelay(5000L) {
            dismissLoadingDialog()
        }
    }


    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    private fun navigateToListFragment(mood: String) {
        val listFragment = ListFragment().apply {
            arguments = Bundle().apply {
                putString(Mood.MOOD, mood)
                putString(Spotify.TOKEN_KEY, token)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, listFragment)
            .addToBackStack(null)
            .commit()
    }
}