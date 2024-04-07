package com.example.instafameproj.ui.userprofile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instafameproj.MainActivity
import com.example.instafameproj.R
import com.example.instafameproj.databinding.ActivityMainBinding
import com.example.instafameproj.databinding.FragmentUserBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val viewModel: UserProfileViewModel by activityViewModels()
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: VideosAdapter
    private lateinit var player: ExoPlayer
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userProfileViewModel =
            ViewModelProvider(this).get(UserProfileViewModel::class.java)
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        val mainActivity = (requireActivity() as MainActivity)
        player = ExoPlayer.Builder(requireContext()).build()
        binding.videoView.player= player
        binding.userNameTV.text = viewModel.getCurrentAuthUser().name

        binding.editProfileBT.setOnClickListener {
            findNavController().navigate(R.id.action_toEditProfile)
        }

        binding.test.setOnClickListener {
            val resUri =
                Uri.parse(("android.resource://" +  mainActivity.packageName.toString() + "/" + R.raw.test1))
            viewModel.uploadVideos(resUri)
        }

        binding.test1.setOnClickListener {

            viewModel.fetchVideos(){
                val s = it[0].downloadUrl.addOnSuccessListener {
                    Log.d("path", it.toString())

                    //val videoUri = Uri.parse(it)
                    val mediaItem = MediaItem.fromUri(it)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                }

            }



        }

        viewModel.observeUserName().observe(viewLifecycleOwner){
            binding.userNameTV.text = it
            viewModel.updateUserMetaUserName(it)
        }

        viewModel.observeQuotes().observe(viewLifecycleOwner){
            binding.quotes.text = it
            viewModel.updateUserMetaQuote(it)
        }

        viewModel.observeUserMeta().observe(viewLifecycleOwner){
            viewModel.setQuotes(it.quotes)
            viewModel.setUserName(it.ownerName)
        }
        initRecyclerViewGrid()

    }
    private fun initRecyclerViewGrid() {
        // Define a layout for RecyclerView
        // Initialize a new instance of RecyclerView Adapter instance
        adapter = VideosAdapter(viewModel){

        }
        binding.videosRV.adapter = adapter
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.videosRV.layoutManager = layoutManager


        // Set the adapter for RecyclerView
        binding.videosRV.layoutManager = LinearLayoutManager(binding.videosRV.context)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}