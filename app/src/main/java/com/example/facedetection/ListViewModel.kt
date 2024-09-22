package com.example.facedetection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facedetection.SearchType.PLAYLIST
import com.example.facedetection.Spotify.GENRE_TYPE
import com.example.facedetection.Spotify.TOKEN_TYPE
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    private val _playlistsWithUsers = MutableLiveData<List<PlaylistWithUser>>()
    val playlistsWithUsers: LiveData<List<PlaylistWithUser>> = _playlistsWithUsers

    private val spotifyApiService = SpotifyRetrofitInstance.apiService

    fun searchPlaylistsAndUsers(genre: String, accessToken: String) {
        viewModelScope.launch {
            try {
                val playlists = spotifyApiService
                    .searchPlaylists(TOKEN_TYPE + accessToken, GENRE_TYPE + genre, PLAYLIST)
                    .playlists
                    .items

                val playlistsWithUsers = playlists.map { playlist ->
                    val user = spotifyApiService
                        .getUser(TOKEN_TYPE + accessToken, playlist.owner.id)

                    PlaylistWithUser(playlist, user)
                }

                _playlistsWithUsers.value = playlistsWithUsers

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}