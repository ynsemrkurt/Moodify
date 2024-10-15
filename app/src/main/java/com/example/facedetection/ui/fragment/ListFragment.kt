package com.example.facedetection.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.facedetection.R
import com.example.facedetection.databinding.FragmentListBinding
import com.example.facedetection.ui.adapter.PlaylistAdapter
import com.example.facedetection.ui.adapter.TrackAdapter
import com.example.facedetection.ui.utils.GenreLists.happyGenres
import com.example.facedetection.ui.utils.GenreLists.neutralGenres
import com.example.facedetection.ui.utils.GenreLists.sadGenres
import com.example.facedetection.ui.utils.GenreLists.tiredGenres
import com.example.facedetection.ui.utils.Mood
import com.example.facedetection.ui.utils.Mood.HAPPY
import com.example.facedetection.ui.utils.Mood.SAD
import com.example.facedetection.ui.utils.Mood.TIRED
import com.example.facedetection.ui.utils.Permission.SUCCESS
import com.example.facedetection.ui.utils.Spotify
import com.example.facedetection.ui.utils.showToast
import com.example.facedetection.ui.viewModel.ListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by viewModels()
    private lateinit var accessToken: String
    private lateinit var mood: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startShimmer()
        setMoodFace()
        searchList()
        searchTracks()
        observePlaylistsWithUsers()
        observeResult()
        observeTracks()
    }

    private fun startShimmer() {
        binding.scrollView2.visibility = View.INVISIBLE
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.shimmerContainer.startShimmer()
    }

    private fun stopShimmer() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            binding.scrollView2.visibility = View.VISIBLE
            binding.shimmerContainer.visibility = View.GONE
            binding.shimmerContainer.stopShimmer()
        }
    }


    private fun setMoodFace() {
        mood = arguments?.getString(Mood.MOOD).toString()
        binding.imageViewFace.setImageResource(moodFaceMap[mood] ?: R.drawable.nuetral_face)
    }

    private val moodFaceMap = mapOf(
        HAPPY to R.drawable.happy_face,
        SAD to R.drawable.sad_face,
        TIRED to R.drawable.tired_face
    )

    private fun searchList() {
        accessToken = arguments?.getString(Spotify.TOKEN_KEY).toString()
        val genres = getGenresForMood(mood)

        viewModel.searchPlaylistsAndUsers(genres, accessToken)
    }

    private fun searchTracks() {
        viewModel.searchTracks(mood, accessToken)
    }

    private fun getGenresForMood(mood: String): String {
        return when (mood) {
            HAPPY -> happyGenres
            SAD -> sadGenres
            TIRED -> tiredGenres
            else -> neutralGenres
        }.random()
    }

    private fun observePlaylistsWithUsers() {
        viewModel.playlistsWithUsers.observe(viewLifecycleOwner) { playlistsWithUsers ->
            binding.recyclerViewPlayList.adapter =
                PlaylistAdapter(playlistsWithUsers) { playlistId ->
                    viewModel.followPlaylist(playlistId, accessToken)
                }
            binding.recyclerViewPlayList.apply {
                set3DItem(true)
                setAlpha(true)
            }
            stopShimmer()
        }
    }

    private fun observeTracks() {
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            binding.recyclerViewTrack.adapter = TrackAdapter(tracks) { trackId ->
                viewModel.followTrack(trackId, accessToken)
            }
            binding.recyclerViewTrack.apply {
                set3DItem(true)
                setAlpha(true)
            }
        }
    }

    private fun observeResult() {
        viewModel.result.observe(viewLifecycleOwner) { result ->
            val message = when (result) {
                R.string.playlist_followed_successfully -> {
                    SuccessDialogFragment(isTrackAction = false).show(
                        parentFragmentManager,
                        SUCCESS
                    )
                    null
                }

                R.string.track_followed_successfully -> {
                    SuccessDialogFragment(isTrackAction = true).show(
                        parentFragmentManager,
                        SUCCESS
                    )
                    null
                }

                else -> result
            }
            message?.let { requireContext().showToast(getString(it)) }
        }
    }
}
