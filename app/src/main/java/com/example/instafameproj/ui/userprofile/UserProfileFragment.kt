package com.example.instafameproj.ui.userprofile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.instafameproj.MainActivity
import com.example.instafameproj.R
import com.example.instafameproj.ui.Model.VideoModel
import com.example.instafameproj.databinding.ActivityMainBinding
import com.example.instafameproj.databinding.FragmentUserBinding

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val viewModel: UserProfileViewModel by activityViewModels()
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: UserVideosListAdapter
    private var test = 0
    private val binding get() = _binding!!

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
        val mainActivity = (requireActivity() as MainActivity)
        binding.userNameTV.text = viewModel.getCurrentAuthUser().name

        binding.editProfileBT.setOnClickListener {
            findNavController().navigate(R.id.action_toEditProfile)
        }

        binding.test.setOnClickListener {
            if(test == 0){
                val resUri =
                    Uri.parse(("android.resource://" +  mainActivity.packageName.toString() + "/" + R.raw.test1))
                viewModel.uploadVideos(resUri,viewModel.getUserMeta().uuid)
                test++
            }
            else if (test == 1) {
                val resUri =
                    Uri.parse(("android.resource://" + mainActivity.packageName.toString() + "/" + R.raw.test2))
                viewModel.uploadVideos(resUri,viewModel.getUserMeta().uuid)
                test++
            }
            else if(test == 2){
                val resUri =
                    Uri.parse(("android.resource://" + mainActivity.packageName.toString() + "/" + R.raw.test3))
                viewModel.uploadVideos(resUri,viewModel.getUserMeta().uuid)
                test++
            }
            else if(test == 3){
                val resUri =
                    Uri.parse(("android.resource://" + mainActivity.packageName.toString() + "/" + R.raw.test4))
                viewModel.uploadVideos(resUri,viewModel.getUserMeta().uuid)
                test++
            }
            else if(test == 4){
                val resUri =
                    Uri.parse(("android.resource://" + mainActivity.packageName.toString() + "/" + R.raw.test5))
                viewModel.uploadVideos(resUri,viewModel.getUserMeta().uuid)
                test++
            }
            else if(test == 5){
                val resUri =
                    Uri.parse(("android.resource://" + mainActivity.packageName.toString() + "/" + R.raw.test6))
                viewModel.uploadVideos(resUri,viewModel.getUserMeta().uuid)
                test++
            }

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
            viewModel.setQuotes(it.quotes)
            viewModel.setUserName(it.userName)
/*
            viewModel.fetchVideos(it.uuid){
                Log.d("video is fetch", "fsdfds")
                adapter.submitList(it)
            }

 */

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}