package com.example.facedetection.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.facedetection.BuildConfig
import com.example.facedetection.R
import com.example.facedetection.ui.fragment.MoodFragment
import com.example.facedetection.ui.utils.Spotify
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    fun loginWithSpotify() {
        val builder = AuthorizationRequest.Builder(
            BuildConfig.CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            BuildConfig.REDIRECT_URI
        )
        builder.setScopes(arrayOf(Spotify.STREAMING_KEY))
        val request = builder.build()

        AuthorizationClient.openLoginInBrowser(this, request)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri: Uri? = intent.data
        uri?.let {
            val response = AuthorizationResponse.fromUri(it)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    val moodFragment = MoodFragment().apply {
                        arguments = Bundle().apply {
                            putString(Spotify.TOKEN_KEY, response.accessToken)
                        }
                    }

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, moodFragment)
                        .addToBackStack(null)
                        .commit()
                }

                AuthorizationResponse.Type.ERROR -> {
                    Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}