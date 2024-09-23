package com.example.facedetection.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.facedetection.data.model.PlaylistWithUser
import com.example.facedetection.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val playlistsWithUsers: List<PlaylistWithUser>) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistAdapterViewHolder>() {

    inner class PlaylistAdapterViewHolder(val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistAdapterViewHolder {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistAdapterViewHolder, position: Int) =
        with(holder.binding) {
            val playlistWithUser = playlistsWithUsers[position]

            textViewListName.text = playlistWithUser.playlist.name
            textViewListDescription.text = playlistWithUser.user.displayName

            Glide.with(holder.itemView.context)
                .load(playlistWithUser.user.images.firstOrNull()?.url)
                .into(imageViewProfile)

            Glide.with(holder.itemView.context)
                .load(playlistWithUser.playlist.images.firstOrNull()?.url)
                .into(imageViewList)

            imageViewProfile.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(playlistWithUser.playlist.externalUrls.spotify)
                )
                holder.itemView.context.startActivity(intent)
            }

            root.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(playlistWithUser.playlist.externalUrls.spotify)
                )
                holder.itemView.context.startActivity(intent)
            }
        }

    override fun getItemCount(): Int = playlistsWithUsers.size
}