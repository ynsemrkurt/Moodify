package com.example.facedetection

import com.example.facedetection.Spotify.DEF_TYPE
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
    ): SpotifySearchResponse
}

const val SEARCH = "search"
const val SEARCH_USER = "users/{user_id}"
