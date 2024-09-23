package com.example.facedetection.data.network

import com.example.facedetection.data.model.SpotifySearchResponse
import com.example.facedetection.data.model.User
import com.example.facedetection.ui.utils.Spotify.DEF_TYPE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {
    @GET(SEARCH)
    suspend fun searchPlaylists(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = DEF_TYPE,
    ): SpotifySearchResponse

    @GET(SEARCH_USER)
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("user_id") userId: String
    ): User
}

const val SEARCH = "search"
const val SEARCH_USER = "users/{user_id}"
