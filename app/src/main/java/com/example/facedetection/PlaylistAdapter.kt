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

    inner class PlaylistViewHolder(val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.binding.textViewListName.text = playlist.name

        Glide.with(holder.itemView.context)
            .load(playlist.images.firstOrNull()?.url)
            .into(holder.binding.imageViewList)

        holder.binding.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playlist.externalUrls.spotify))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}
