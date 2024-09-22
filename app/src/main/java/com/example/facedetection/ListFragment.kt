package com.example.facedetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.facedetection.GenreLists.happyGenres
import com.example.facedetection.GenreLists.neutralGenres
import com.example.facedetection.GenreLists.sadGenres
import com.example.facedetection.GenreLists.tiredGenres
import com.example.facedetection.Mood.HAPPY
import com.example.facedetection.Mood.MOOD
import com.example.facedetection.Mood.SAD
import com.example.facedetection.Mood.TIRED
import com.example.facedetection.Spotify.TOKEN_KEY
import com.example.facedetection.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchList()
        observePlaylists()
    }

    private fun searchList() {
        val accessToken = arguments?.getString(TOKEN_KEY)
        val mood = arguments?.getString(MOOD).toString()

        val genres = getGenresForMood(mood)

        accessToken?.let { token ->
            viewModel.searchPlaylists(genres, token)
        }
    }

    private fun getGenresForMood(mood: String): String {
        return when (mood) {
            HAPPY -> happyGenres
            SAD -> sadGenres
            TIRED -> tiredGenres
            else -> neutralGenres
        }.random()
    }

    private fun observePlaylists() {
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            binding.recyclerViewPlayList.adapter = PlaylistAdapter(playlists)
        }
    }
}