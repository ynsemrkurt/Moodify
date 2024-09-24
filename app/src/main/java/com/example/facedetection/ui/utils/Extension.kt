package com.example.facedetection.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun FragmentActivity.replaceFragment(
    containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true
) {
    supportFragmentManager.beginTransaction().apply {
        replace(containerId, fragment)
        if (addToBackStack) addToBackStack(null)
        commit()
    }
}

fun Context.openUrlInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

fun ImageView.loadImage(url: String?, placeholder: Int, errorImage: Int) {
    Glide.with(this.context)
        .load(url)
        .placeholder(placeholder)
        .error(errorImage)
        .into(this)
}