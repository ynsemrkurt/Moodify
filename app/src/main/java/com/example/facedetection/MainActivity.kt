package com.example.facedetection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {

    private lateinit var moodTextView: TextView
    private lateinit var imageView: ImageView

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            imageView.setImageURI(uri)
            analyzeSelectedImage(uri)
        } else {
            moodTextView.text = "Fotoğraf seçilmedi."
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moodTextView = findViewById(R.id.moodTextView)
        imageView = findViewById(R.id.imageView)
        val selectPhotoButton = findViewById<Button>(R.id.selectPhotoButton)

        selectPhotoButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
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
                imageView.setImageURI(imageUri)
                analyzeSelectedImage(imageUri)
            }
        }
    }

    private fun analyzeSelectedImage(imageUri: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
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
                        moodTextView.text = "Yüz algılanmadı."
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FaceDetection", "Yüz algılama başarısız oldu", e)
                    moodTextView.text = "Analiz sırasında hata oluştu."
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
            smilingProb > 0.5f -> "Mutlu"
            smilingProb < 0.3f -> "Üzgün"
            leftEyeOpenProb < 0.5f && rightEyeOpenProb < 0.5f -> "Yorgun"
            else -> "Nötr"
        }

        runOnUiThread {
            moodTextView.text = "Ruh Hali: $mood"
        }
    }
}