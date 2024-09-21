package com.example.facedetection

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApiService {
    @GET(SEARCH)
    suspend fun searchPlaylists(
        @Header("Authorization") token: String,
        @Query("q") query: String,
        @Query("type") type: String = "playlist"
    ): SpotifySearchResponse
}

const val SEARCH = "search"
