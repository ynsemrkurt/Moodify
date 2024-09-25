package com.example.facedetection.ui.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facedetection.ui.utils.Permission.CAMERA_PERMISSION_CODE
import com.example.facedetection.ui.utils.Permission.REQUEST_IMAGE_CAPTURE
import com.google.android.material.snackbar.Snackbar

class PermissionManager(private val activity: Activity, private val rootView: View) {

    fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_IMAGE_CAPTURE
        )
    }

    fun handlePermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted()
                } else {
                    showPermissionDeniedSnackbar("Kamera izni reddedildi. Lütfen izin verin.") {
                        requestCameraPermission()
                    }
                    onPermissionDenied()
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted()
                } else {
                    showPermissionDeniedSnackbar("Depolama izni reddedildi. Lütfen izin verin.") {
                        requestStoragePermission()
                    }
                    onPermissionDenied()
                }
            }
        }
    }

    private fun showPermissionDeniedSnackbar(message: String, action: () -> Unit) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setAction("İzin Ver") {
                action()
            }.show()
    }
}