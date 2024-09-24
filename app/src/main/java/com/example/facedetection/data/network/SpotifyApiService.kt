package com.example.facedetection.data.network

import com.example.facedetection.data.model.FollowPlaylistRequest
import com.example.facedetection.data.model.SpotifySearchResponse
import com.example.facedetection.data.model.TrackResponse
import com.example.facedetection.data.model.User
import com.example.facedetection.ui.utils.SearchType.PLAYLIST
import com.example.facedetection.ui.utils.SearchType.TRACKS
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {
    @GET(SEARCH)
    suspend fun searchPlaylists(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = PLAYLIST,
    ): SpotifySearchResponse

    @GET(SEARCH)
    suspend fun searchTracks(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = TRACKS,
    ): TrackResponse

    @GET(SEARCH_USER)
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): User

    @PUT(FOLLOW_PLAYLIST)
    suspend fun followPlaylist(
        @Header("Authorization") token: String,
        @Path("playlist_id") playlistId: String,
        @Body followPlaylistRequest: FollowPlaylistRequest = FollowPlaylistRequest(false)
    )
}

const val SEARCH = "search"
const val SEARCH_USER = "users/{user_id}"
const val FOLLOW_PLAYLIST = "playlists/{playlist_id}/followers"
