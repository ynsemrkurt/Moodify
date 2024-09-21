package com.example.facedetection

import com.google.gson.annotations.SerializedName

data class SpotifySearchResponse(
    @SerializedName("playlists")
    val playlists: Playlists,
)

data class Playlists(
    @SerializedName("items")
    val items: List<PlaylistItem>,
)

data class PlaylistItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls,
)

data class Image(
    @SerializedName("url")
    val url: String,
)

data class ExternalUrls(
    @SerializedName("spotify")
    val spotify: String,
)
