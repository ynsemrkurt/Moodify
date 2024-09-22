package com.example.facedetection

import com.example.facedetection.Spotify.DEF_NAME
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
    @SerializedName("type")
    val type: String,
    @SerializedName("owner")
    val owner: Owner,
)

data class Owner(
    @SerializedName("id")
    val id: String,
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls,
    @SerializedName("display_name")
    val displayName: String? = DEF_NAME,
)

data class Image(
    @SerializedName("url")
    val url: String,
)

data class ExternalUrls(
    @SerializedName("spotify")
    val spotify: String,
)