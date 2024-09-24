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
    @SerializedName("public")
    val public: Boolean,
)

data class FollowTrackRequest(
    @SerializedName("ids")
    val ids: List<String>,
)

data class TrackResponse(
    @SerializedName("tracks")
    val tracks: Tracks
)

data class Tracks(
    @SerializedName("items")
    val items: List<TrackItem>
)

data class TrackItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls,
    @SerializedName("artists")
    val artists: List<Artist>,
    @SerializedName("album")
    val album: Album,
)

data class Album(
    @SerializedName("images")
    val images: List<Image>,
)

data class Artist(
    @SerializedName("external_urls")
    val externalUrls: ExternalUrls,
    @SerializedName("name")
    val name: String,
)