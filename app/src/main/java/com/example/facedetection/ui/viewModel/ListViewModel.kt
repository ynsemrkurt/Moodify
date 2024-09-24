package com.example.facedetection.ui.viewModel

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.facedetection.R
import com.example.facedetection.data.model.FollowTrackRequest
import com.example.facedetection.data.model.PlaylistWithUser
import com.example.facedetection.data.model.TrackItem
import com.example.facedetection.data.network.SpotifyRetrofitInstance
import com.example.facedetection.ui.utils.Spotify.GENRE_TYPE
import com.example.facedetection.ui.utils.Spotify.TOKEN_TYPE
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {

    private val _playlistsWithUsers = MutableLiveData<List<PlaylistWithUser>>()
    val playlistsWithUsers: LiveData<List<PlaylistWithUser>> = _playlistsWithUsers

    private val _tracks = MutableLiveData<List<TrackItem>>()
    val tracks: LiveData<List<TrackItem>> = _tracks

    private val _result = MutableLiveData<@receiver:StringRes Int>()
    val result: LiveData<Int> = _result

    private val spotifyApiService = SpotifyRetrofitInstance.apiService

    fun searchPlaylistsAndUsers(genre: String, accessToken: String) {
        viewModelScope.launch {
            try {
                val playlists = spotifyApiService.searchPlaylists(
                    TOKEN_TYPE + accessToken, GENRE_TYPE + genre
                ).playlists.items

                val playlistsWithUsers = playlists.map { playlist ->
                    async {
                        val user =
                            spotifyApiService.getUser(TOKEN_TYPE + accessToken, playlist.owner.id)
                        PlaylistWithUser(playlist, user)
                    }
                }.awaitAll()

                _playlistsWithUsers.value = playlistsWithUsers

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun searchTracks(query: String, accessToken: String) {
        viewModelScope.launch {
            try {
                val tracks = spotifyApiService.searchTracks(
                    TOKEN_TYPE + accessToken, GENRE_TYPE + query
                ).tracks.items

                _tracks.value = tracks
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun followPlaylist(playlistId: String, accessToken: String) {
        viewModelScope.launch {
            try {
                spotifyApiService.followPlaylist(
                    TOKEN_TYPE + accessToken, playlistId = playlistId
                )
                _result.value = R.string.playlist_followed_successfully
            } catch (e: Exception) {
                e.printStackTrace()
                _result.value = R.string.failed_to_follow_playlist
            }
        }
    }

    fun followTrack(trackId: String, accessToken: String) {
        viewModelScope.launch {
            try {
                spotifyApiService.followTrack(
                    TOKEN_TYPE + accessToken,
                    listOf(trackId),
                    FollowTrackRequest(listOf(trackId))
                )
                _result.value = R.string.track_followed_successfully
            } catch (e: Exception) {
                e.printStackTrace()
                _result.value = R.string.failed_to_follow_track
            }
        }
    }
}