package com.example.facedetection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.facedetection.Mood.HAPPY
import com.example.facedetection.Mood.MOOD
import com.example.facedetection.Mood.NEUTRAL
import com.example.facedetection.Mood.SAD
import com.example.facedetection.Mood.TIRED
import com.example.facedetection.Spotify.TOKEN_KEY
import com.example.facedetection.databinding.FragmentMoodBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

class MoodFragment : Fragment() {

    private lateinit var binding: FragmentMoodBinding
    private var loadingDialog: AlertDialog? = null
    private val executor = Executors.newSingleThreadExecutor()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        token = arguments?.getString(TOKEN_KEY)
        binding = FragmentMoodBinding.inflate(inflater)
        return binding.root
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                binding.imageView.setImageURI(it)
                showLoadingDialog()
                analyzeSelectedImage(it)
            } ?: run {
                binding.moodTextView.text = getString(R.string.no_photo_selected)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectPhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                checkAndRequestPermissions()
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == RESULT_OK) {
            data?.data?.let { imageUri ->
                binding.imageView.setImageURI(imageUri)
                showLoadingDialog()
                analyzeSelectedImage(imageUri)
            }
        }
    }

    private fun showLoadingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        loadingDialog?.show()

        executor.execute {
            Thread.sleep(5000)
            requireActivity().runOnUiThread { loadingDialog?.dismiss() }
        }
    }

    private fun analyzeSelectedImage(imageUri: Uri) {
        try {
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            val detector = FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build()
            )

            detector.process(inputImage)
                .addOnSuccessListener { faces -> handleFaceDetectionResult(faces) }
                .addOnFailureListener { e ->
                    binding.moodTextView.text =
                        getString(R.string.an_error_occurred_during_analysis, e.message)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleFaceDetectionResult(faces: List<Face>) {
        if (faces.isEmpty()) {
            binding.moodTextView.text = getString(R.string.no_face_detected)
            return
        }

        faces.forEach { face ->
            val mood = analyzeFaceMood(face)
            navigateToListFragment(mood)
        }
    }

    private fun analyzeFaceMood(face: Face): String {
        val smilingProb = face.smilingProbability ?: 0.0f
        val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0.0f
        val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0.0f

        return when {
            smilingProb > 0.6f -> HAPPY
            smilingProb < 0.3f -> SAD
            leftEyeOpenProb < 0.5f && rightEyeOpenProb < 0.5f -> TIRED
            else -> NEUTRAL
        }
    }

    private fun navigateToListFragment(mood: String) {
        requireActivity().runOnUiThread {
            val listFragment = ListFragment().apply {
                arguments = Bundle().apply {
                    putString(MOOD, mood)
                    putString(TOKEN_KEY, token)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, listFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}