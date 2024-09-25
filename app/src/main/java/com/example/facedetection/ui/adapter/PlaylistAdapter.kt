package com.example.facedetection.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facedetection.R
import com.example.facedetection.data.model.PlaylistWithUser
import com.example.facedetection.databinding.ItemPlaylistBinding
import com.example.facedetection.ui.utils.loadImage
import com.example.facedetection.ui.utils.openUrlInBrowser

class PlaylistAdapter(
    private val playlistsWithUsers: List<PlaylistWithUser>,
    private val onAddListClick: (String) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistAdapterViewHolder>() {

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
            textViewListArtist.text = playlistWithUser.user.displayName

            imageViewProfile.loadImage(
                playlistWithUser.user.images.firstOrNull()?.url,
                R.drawable.man_profile,
                R.drawable.man_profile
            )

            imageViewList.loadImage(
                playlistWithUser.playlist.images.firstOrNull()?.url,
                R.drawable.image_32,
                R.drawable.image_32
            )

            imageViewProfile.setOnClickListener {
                holder.itemView.context.openUrlInBrowser(playlistWithUser.user.externalUrls.spotify)
            }

            imageButtonPlay.setOnClickListener {
                holder.itemView.context.openUrlInBrowser(playlistWithUser.playlist.externalUrls.spotify)
            }

            imageButtonAdd.setOnClickListener {
                onAddListClick(playlistWithUser.playlist.id)
            }
        }

    override fun getItemCount(): Int = playlistsWithUsers.size
}