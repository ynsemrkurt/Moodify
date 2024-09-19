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

    companion object {
        const val HAPPY = "Happy"
        const val SAD = "Sad"
        const val TIRED = "Tired"
        const val NEUTRAL = "Neutral"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoodBinding.inflate(inflater)
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                binding.imageView.setImageURI(uri)
                showLoadingDialog()
                analyzeSelectedImage(uri)
            } else {
                binding.moodTextView.text = getString(R.string.no_photo_selected)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectPhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
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
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 102)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 102 && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.imageView.setImageURI(imageUri)
                showLoadingDialog()
                analyzeSelectedImage(imageUri)
            }
        }
    }

    private fun showLoadingDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, null)
        builder.setView(dialogView)
        loadingDialog = builder.create()
        loadingDialog?.show()
        executor.execute {
            Thread.sleep(5000)
            requireActivity().runOnUiThread {
                loadingDialog?.dismiss()
            }
        }
    }

    private fun analyzeSelectedImage(imageUri: Uri) {
        try {
            val bitmap =
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            val image = InputImage.fromBitmap(bitmap, 0)

            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

            val detector = FaceDetection.getClient(options)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        for (face in faces) {
                            analyzeFace(face)
                        }
                    } else {
                        binding.moodTextView.text = getString(R.string.no_face_detected)
                    }
                }
                .addOnFailureListener { e ->
                    binding.moodTextView.text =
                        getString(R.string.an_error_occurred_during_analysis, e.message)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun analyzeFace(face: Face) {
        val smilingProb = face.smilingProbability ?: 0.0f
        val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0.0f
        val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0.0f

        val mood = when {
            smilingProb > 0.6f -> HAPPY
            smilingProb < 0.3f -> SAD
            leftEyeOpenProb < 0.5f && rightEyeOpenProb < 0.5f -> TIRED
            else -> NEUTRAL
        }

        requireActivity().runOnUiThread {
            binding.moodTextView.text = getString(R.string.mood, mood)
        }
    }
}