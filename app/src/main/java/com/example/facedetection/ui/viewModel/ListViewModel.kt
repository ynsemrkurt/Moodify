package com.example.facedetection.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facedetection.data.model.PlaylistWithUser
import com.example.facedetection.data.network.SpotifyRetrofitInstance
import com.example.facedetection.ui.utils.SearchType
import com.example.facedetection.ui.utils.Spotify
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    private val _playlistsWithUsers = MutableLiveData<List<PlaylistWithUser>>()
    val playlistsWithUsers: LiveData<List<PlaylistWithUser>> = _playlistsWithUsers

    private val spotifyApiService = SpotifyRetrofitInstance.apiService

    fun searchPlaylistsAndUsers(genre: String, accessToken: String) {
        viewModelScope.launch {
            try {
                val playlists = spotifyApiService
                    .searchPlaylists(
                        Spotify.TOKEN_TYPE + accessToken, Spotify.GENRE_TYPE + genre,
                        SearchType.PLAYLIST
                    )
                    .playlists
                    .items

                val playlistsWithUsers = playlists.map { playlist ->
                    val user = spotifyApiService
                        .getUser(Spotify.TOKEN_TYPE + accessToken, playlist.owner.id)

                    PlaylistWithUser(playlist, user)
                }

                _playlistsWithUsers.value = playlistsWithUsers

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}