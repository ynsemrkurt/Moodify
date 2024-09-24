package com.example.facedetection.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.facedetection.ui.utils.Mood
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MoodViewModel : ViewModel() {
    private val _mood = MutableLiveData<String>()
    val mood: LiveData<String> = _mood

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun analyzeSelectedImage(context: Context, imageUri: Uri) {
        try {
            val bitmap = getBitmapFromUri(context, imageUri)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            val detector = FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build()
            )

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    handleFaceDetectionResult(faces)
                }
                .addOnFailureListener { e ->
                    _error.value = "Error: ${e.message}"
                }
        } catch (e: Exception) {
            e.printStackTrace()
            _error.value = "Error: ${e.message}"
        }
    }

    private fun getBitmapFromUri(context: Context, imageUri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    }

    private fun handleFaceDetectionResult(faces: List<Face>) {
        if (faces.isEmpty()) {
            _error.value = "No face detected"
        } else {
            faces.forEach { face ->
                val detectedMood = analyzeFaceMood(face)
                _mood.value = detectedMood
            }
        }
    }

    private fun analyzeFaceMood(face: Face): String {
        val smilingProb = face.smilingProbability ?: 0.0f
        val leftEyeOpenProb = face.leftEyeOpenProbability ?: 0.0f
        val rightEyeOpenProb = face.rightEyeOpenProbability ?: 0.0f

        return when {
            smilingProb > 0.6f -> Mood.HAPPY
            smilingProb < 0.3f -> Mood.SAD
            leftEyeOpenProb < 0.5f && rightEyeOpenProb < 0.5f -> Mood.TIRED
            else -> Mood.NEUTRAL
        }
    }

    fun executeWithDelay(delayMillis: Long, action: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            action()
        }, delayMillis)
    }

    fun analyzeSelectedImageFromBitmap(bitmap: Bitmap) {
        try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            val detector = FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build()
            )

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    handleFaceDetectionResult(faces)
                }
                .addOnFailureListener { e ->
                    _mood.value = "Error: ${e.message}"
                }
        } catch (e: Exception) {
            e.printStackTrace()
            _mood.value = "Error: ${e.message}"
        }
    }
}