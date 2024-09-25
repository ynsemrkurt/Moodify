package com.example.facedetection.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.facedetection.R
import com.google.android.material.snackbar.Snackbar

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

fun <T : Parcelable> Intent.getParcelable(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        extras?.getParcelable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        extras?.getParcelable(key)
    }
}

fun View.showSnackbar(@StringRes message: Int, callback: () -> Unit) {
    Snackbar.make(this, getString(this.context, message), Snackbar.LENGTH_LONG)
        .setAction(getString(this.context, R.string.open_settings)) {
            callback()
        }.show()
}