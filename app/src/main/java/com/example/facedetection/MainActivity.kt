package com.example.facedetection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.facedetection.BuildConfig.CLIENT_ID
import com.example.facedetection.BuildConfig.REDIRECT_URI
import com.example.facedetection.Spotify.STREAMING_KEY
import com.example.facedetection.Spotify.TOKEN_KEY
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
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI
        )
        builder.setScopes(arrayOf(STREAMING_KEY))
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
                            putString(TOKEN_KEY, response.accessToken)
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