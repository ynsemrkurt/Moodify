package com.example.facedetection.data.model

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
    @SerializedName("id")
    val id: String,
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
)

data class User(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls,
    @SerializedName("images")
    val images: List<Image>,
)

data class Image(
    @SerializedName("url")
    val url: String,
)

data class ExternalUrls(
    @SerializedName("spotify")
    val spotify: String,
)

data class PlaylistWithUser(
    val playlist: PlaylistItem,
    val user: User
)

data class FollowPlaylistRequest(
    val public: Boolean
)