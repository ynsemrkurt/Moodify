package com.example.facedetection.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.facedetection.R
import com.example.facedetection.databinding.SuccessfulDialogBinding
import com.example.facedetection.ui.utils.Spotify.GO_PLAYLIST
import com.example.facedetection.ui.utils.Spotify.GO_TRACKS

class SuccessDialogFragment(private val isTrackAction: Boolean) : DialogFragment() {

    private lateinit var binding: SuccessfulDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SuccessfulDialogBinding.inflate(inflater, container, false)

        with(binding) {
            goSpotifyButton.setOnClickListener {
                val intent = if (isTrackAction) {
                    Intent(Intent.ACTION_VIEW, Uri.parse(GO_TRACKS))
                } else {
                    Intent(Intent.ACTION_VIEW, Uri.parse(GO_PLAYLIST))
                }
                startActivity(intent)
                dismiss()
            }

            textViewSuccess.text = if (isTrackAction) {
                getString(R.string.successful_track_text)
            } else {
                getString(R.string.successful_text)
            }

            dismissButton.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onResume() {
        super.onResume()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }
}