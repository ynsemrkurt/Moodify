package com.example.facedetection.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.facedetection.BuildConfig
import com.example.facedetection.R
import com.example.facedetection.ui.fragment.MoodFragment
import com.example.facedetection.ui.utils.Spotify
import com.example.facedetection.ui.utils.Spotify.MODIFY_LIBRARY_KEY
import com.example.facedetection.ui.utils.Spotify.MODIFY_PLAYLIST_PRIVATE_KEY
import com.example.facedetection.ui.utils.Spotify.MODIFY_PLAYLIST_PUBLIC_KEY
import com.example.facedetection.ui.utils.replaceFragment
import com.example.facedetection.ui.utils.showToast
import com.google.android.gms.ads.MobileAds
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }
    }

    fun loginWithSpotify() {
        val request = createAuthorizationRequest()
        AuthorizationClient.openLoginInBrowser(this, request)
    }

    private fun createAuthorizationRequest(): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            BuildConfig.CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            BuildConfig.REDIRECT_URI
        ).setScopes(
            arrayOf(
                MODIFY_LIBRARY_KEY,
                MODIFY_PLAYLIST_PRIVATE_KEY,
                MODIFY_PLAYLIST_PUBLIC_KEY
            )
        ).build()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.data?.let { uri ->
            handleAuthorizationResponse(uri)
        }
    }

    private fun handleAuthorizationResponse(uri: Uri) {
        val response = AuthorizationResponse.fromUri(uri)

        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                navigateToMoodFragment(response.accessToken)
            }

            AuthorizationResponse.Type.ERROR -> {
                showToast(response.error)
            }

            else -> {
                showToast(getString(R.string.unknown_error))
            }
        }
    }

    private fun navigateToMoodFragment(accessToken: String) {
        val moodFragment = MoodFragment().apply {
            arguments = Bundle().apply {
                putString(Spotify.TOKEN_KEY, accessToken)
            }
        }

        replaceFragment(R.id.fragmentContainer, moodFragment)
    }
}