package com.example.facedetection.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.facedetection.R
import com.example.facedetection.data.model.TrackItem
import com.example.facedetection.databinding.ItemTrackBinding

class TrackAdapter(
    private val tracks: List<TrackItem>
) :
    RecyclerView.Adapter<TrackAdapter.TrackAdapterViewHolder>() {

    inner class TrackAdapterViewHolder(val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapterViewHolder {
        val binding =
            ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackAdapterViewHolder, position: Int): Unit =
        with(holder.binding) {
            val tracks = tracks[position]

            textViewTrackName.text = tracks.name
            textViewTrackArtist.text = tracks.artists.firstOrNull()?.name

            Glide.with(holder.itemView.context)
                .load(tracks.album.images.firstOrNull()?.url)
                .placeholder(R.drawable.image_32)
                .error(R.drawable.image_32)
                .into(imageViewTrack)
        }

    override fun getItemCount(): Int = tracks.size
}