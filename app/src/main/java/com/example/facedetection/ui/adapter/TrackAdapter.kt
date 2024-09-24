package com.example.facedetection.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facedetection.R
import com.example.facedetection.data.model.TrackItem
import com.example.facedetection.databinding.ItemTrackBinding
import com.example.facedetection.ui.utils.loadImage
import com.example.facedetection.ui.utils.openUrlInBrowser

class TrackAdapter(
    private val tracks: List<TrackItem>,
    private val onAddTrackClick: (String) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackAdapterViewHolder>() {

    inner class TrackAdapterViewHolder(val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapterViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackAdapterViewHolder, position: Int) =
        with(holder.binding) {
            val track = tracks[position]

            textViewTrackName.text = track.name
            textViewTrackArtist.text = track.artists.firstOrNull()?.name

            imageViewTrack.loadImage(
                track.album.images.firstOrNull()?.url,
                R.drawable.image_32,
                R.drawable.image_32
            )

            root.setOnClickListener {
                holder.itemView.context.openUrlInBrowser(track.externalUrls.spotify)
            }

            textViewTrackArtist.setOnClickListener {
                track.artists.firstOrNull()?.externalUrls?.spotify?.let { url ->
                    holder.itemView.context.openUrlInBrowser(url)
                }
            }

            imageButtonAdd.setOnClickListener {
                onAddTrackClick(track.id)
            }
        }

    override fun getItemCount(): Int = tracks.size
}