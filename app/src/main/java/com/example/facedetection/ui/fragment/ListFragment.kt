package com.example.facedetection.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.facedetection.databinding.FragmentListBinding
import com.example.facedetection.ui.adapter.PlaylistAdapter
import com.example.facedetection.ui.utils.GenreLists
import com.example.facedetection.ui.utils.Mood
import com.example.facedetection.ui.utils.Mood.HAPPY
import com.example.facedetection.ui.utils.Mood.SAD
import com.example.facedetection.ui.utils.Mood.TIRED
import com.example.facedetection.ui.utils.Spotify
import com.example.facedetection.ui.viewModel.ListViewModel

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
        observePlaylistsWithUsers()
    }

    private fun searchList() {
        val accessToken = arguments?.getString(Spotify.TOKEN_KEY)
        val mood = arguments?.getString(Mood.MOOD).toString()

        val genres = getGenresForMood(mood)

        accessToken?.let { token ->
            viewModel.searchPlaylistsAndUsers(genres, token)
        }
    }

    private fun getGenresForMood(mood: String): String {
        return when (mood) {
            HAPPY -> GenreLists.happyGenres
            SAD -> GenreLists.sadGenres
            TIRED -> GenreLists.tiredGenres
            else -> GenreLists.neutralGenres
        }.random()
    }

    private fun observePlaylistsWithUsers() {
        viewModel.playlistsWithUsers.observe(viewLifecycleOwner) { playlistsWithUsers ->
            binding.recyclerViewPlayList.adapter = PlaylistAdapter(playlistsWithUsers)
        }
    }
}