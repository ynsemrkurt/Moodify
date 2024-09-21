package com.example.facedetection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : AppCompatActivity() {

    companion object {
        const val REDIRECT_URI = "com.example.facedetection://callback"
        const val CLIENT_ID = "09e91cdade3e42f194664ad025820db5"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun loginWithSpotify() {
        val builder = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            REDIRECT_URI
        )
        builder.setScopes(arrayOf("streaming"))
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
                            putString("TOKEN_KEY", response.accessToken)
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
                    Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}