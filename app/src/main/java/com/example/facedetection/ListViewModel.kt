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

    private val _playlists = MutableLiveData<List<PlaylistItem>>()
    val playlists: LiveData<List<PlaylistItem>> = _playlists

    private val spotifyApiService = SpotifyRetrofitInstance.apiService

    fun searchPlaylists(genre: String, accessToken: String) {
        viewModelScope.launch {
            try {
                _playlists.value = spotifyApiService
                    .searchPlaylists(TOKEN_TYPE + accessToken, GENRE_TYPE + genre, PLAYLIST)
                    .playlists
                    .items

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}