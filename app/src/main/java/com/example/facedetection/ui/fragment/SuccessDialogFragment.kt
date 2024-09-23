package com.example.facedetection.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.facedetection.databinding.SuccessfulDialogBinding

class SuccessDialogFragment : DialogFragment() {

    private lateinit var binding: SuccessfulDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SuccessfulDialogBinding.inflate(inflater, container, false)

        binding.dismissButton.setOnClickListener {
            dismiss()
        }

        binding.goSpotifyButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("spotify:library"))
            startActivity(intent)
            dismiss()
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
