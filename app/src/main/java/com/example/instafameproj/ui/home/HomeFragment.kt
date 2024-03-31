package com.example.instafameproj.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.instafameproj.R
import com.example.instafameproj.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var videoView: VideoView
    private lateinit var mediaControls: MediaController
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.textHome


        videoView = binding.videoView

            // create an object of media controller class
        mediaControls = MediaController(context);
        mediaControls.setAnchorView(videoView);

        videoView.setMediaController(mediaControls)

        val packageName =videoView.context.packageName


        videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.cat_vid))

        videoView.start();






        homeViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}