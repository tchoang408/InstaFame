package com.example.instafameproj.ui.userprofile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.instafameproj.MainActivity
import com.example.instafameproj.R
import com.example.instafameproj.databinding.FragmentUserBinding
import com.example.instafameproj.ui.MediaUpload
import com.example.instafameproj.ui.Model.VideoModel

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val viewModel: UserProfileViewModel by activityViewModels()
    private lateinit var mainActivity: FragmentActivity
    private lateinit var adapter: UserVideosListAdapter
    private val binding get() = _binding!!
    private lateinit var context:Context
    lateinit var mediaUpload: MediaUpload

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        mainActivity = (requireActivity() as MainActivity)
        context = requireContext()

       // binding.userNameTV.text = viewModel.getCurrentAuthUser().name

        binding.editProfileBT.setOnClickListener {
            findNavController().navigate(R.id.action_toEditProfile)
        }

        binding.userProfilePic.setOnClickListener {
            mediaUpload.checkPermissionAndPickPhoto(context,this)

        }


        viewModel.observeUserName().observe(viewLifecycleOwner){
            binding.userNameTV.text = it
            viewModel.updateCurrentUserName(it)
        }

        viewModel.observeQuotes().observe(viewLifecycleOwner){
            binding.quotes.text = it
            viewModel.updateCurrentUserQuote(it)
        }

        viewModel.observeUserMeta().observe(viewLifecycleOwner){
            Log.d("createUserMeta", "Create user meta")

            viewModel.setQuotes(it.quotes)
            viewModel.setUserName(it.ownerName)
            viewModel.setProfilePic(it.profilePic, binding.userProfilePic)
            val list = mutableListOf<VideoModel>()
            for(url in it.videoUrl){
                val videoData = VideoModel(
                    url = url
                )
                list.add(videoData)
            }
            adapter.submitList(list)


        }
        initRecyclerViewGrid()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        mediaUpload = MediaUpload(requireActivity().activityResultRegistry,::uploadMediaListener)
        lifecycle.addObserver(mediaUpload)
    }
    private fun initRecyclerViewGrid() {
        // Define a layout for RecyclerView
        // Initialize a new instance of RecyclerView Adapter instance
        adapter = UserVideosListAdapter(viewModel){

        }
        binding.videosRV.adapter = adapter
       // val layoutManager = GridLayoutManager(this.context, 2,GridLayoutManager.HORIZONTAL,true)
      //  binding.videosRV.layoutManager = layoutManager


        // Set the adapter for RecyclerView
       // binding.videosRV.layoutManager = LinearLayoutManager(binding.videosRV.context)
    }

    fun uploadMediaListener(uri: Uri){
        viewModel.uploadPics(uri,viewModel.getUserMeta().uuid)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        adapter.releasePlayer()
    }

}