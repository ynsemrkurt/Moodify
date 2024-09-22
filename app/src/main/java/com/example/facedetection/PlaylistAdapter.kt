package com.example.facedetection

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.facedetection.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val playlists: List<PlaylistItem>) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        with(holder.binding) {
            bindPlaylistData(playlist, holder)
        }
    }

    override fun getItemCount(): Int = playlists.size

    private fun ItemPlaylistBinding.bindPlaylistData(playlist: PlaylistItem, holder: PlaylistViewHolder) {
        textViewListName.text = playlist.name
        textViewListDescription.text = holder.itemView.context.getString(
            R.string.made_for, playlist.type, playlist.owner.displayName
        )

        Glide.with(holder.itemView.context)
            .load(playlist.images.firstOrNull()?.url)
            .into(imageViewList)

        setClickListeners(playlist, holder)
    }

    private fun ItemPlaylistBinding.setClickListeners(playlist: PlaylistItem, holder: PlaylistViewHolder) {
        textViewListDescription.setOnClickListener {
            openSpotifyLink(playlist.owner.externalUrls.spotify, holder)
        }

        root.setOnClickListener {
            openSpotifyLink(playlist.externalUrls.spotify, holder)
        }
    }

    private fun openSpotifyLink(url: String, holder: PlaylistViewHolder) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        holder.itemView.context.startActivity(intent)
    }
}