package com.example.facedetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.facedetection.databinding.FragmentListBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var spotifyApiService: SpotifyApiService

    val happyGenres = listOf(
        "afrobeat", "bossa nova", "dance", "disco", "funk", "party", "pop",
        "power-pop", "salsa", "samba", "sertanejo", "tango", "tropical"
    )

    val sadGenres = listOf(
        "blues", "emo", "sad", "gospel", "goth", "folk",
        "singer-songwriter", "rainy-day", "ambient"
    )

    val tiredGenres = listOf(
        "acoustic", "chill", "lo-fi", "jazz", "classical", "sleep",
        "study", "new-age", "minimal-techno", "ambient"
    )

    val neutralGenres = listOf(
        "indie", "alternative", "indie-pop", "rock", "classical", "pop",
        "electronic", "techno", "soundtracks", "synth-pop", "world-music"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        spotifyApiService = retrofit.create(SpotifyApiService::class.java)

        binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mood = arguments?.getString("MOOD")
        val accessToken = arguments?.getString("TOKEN_KEY")

        val genres = when (mood) {
            "Happy" -> happyGenres.random()
            "Sad" -> sadGenres.random()
            "Tired" -> tiredGenres.random()
            else -> neutralGenres.random()
        }

        accessToken?.let {
            lifecycleScope.launch {
                searchPlaylists(genres, it)
            }
        }
    }

    private suspend fun searchPlaylists(genre: String, accessToken: String) {
        val token = "Bearer $accessToken" // Ensure accessToken is available

        val call =
            spotifyApiService.searchPlaylists(token, "genre:$genre", "playlist").playlists.items

        lifecycleScope.launch {
            try {

                if (call.isNotEmpty()) {
                    binding.recyclerViewPlayList.adapter = PlaylistAdapter(call)
                } else {
                    // TODO
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle API error
            }
        }
    }
}