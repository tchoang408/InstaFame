package com.example.instafameproj.ui.userprofile

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.instafameproj.MainActivity
import com.example.instafameproj.R
import com.example.instafameproj.databinding.FragmentUserBinding
import com.example.instafameproj.ui.Model.VideoModel

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val viewModel: UserProfileViewModel by activityViewModels()
    private lateinit var mainActivity: FragmentActivity
    private lateinit var adapter: UserVideosListAdapter
    private var test = 0
    private val binding get() = _binding!!
    lateinit var photoLauncher: ActivityResultLauncher<Intent>
    private lateinit var context:Context

    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Photo selected, handle the Uri accordingly
            viewModel.uploadPics(uri,viewModel.getUserMeta().uuid)
        }
    }
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
            checkPermissionAndPickPhoto()
            Log.d("upload_picture", "success")

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

    fun checkPermissionAndPickPhoto(){
        var readExternalPhoto : String = ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readExternalPhoto = android.Manifest.permission.READ_MEDIA_IMAGES
        }else{
            readExternalPhoto = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if(ContextCompat.checkSelfPermission(context,readExternalPhoto)== PackageManager.PERMISSION_GRANTED){
            //we have permission
            openPhotoPicker()
            Log.d("permission_granted", "granted")
        }else{
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(readExternalPhoto),
                100
            )
        }
    }

    private fun  openPhotoPicker(){

        // Registers a photo picker activity launcher in single-select mode.
        Environment.getExternalStorageDirectory()
        Log.d("dire", Environment.getExternalStorageDirectory().toString())
        getContent.launch("image/*")


    }
    override fun onStop() {
        super.onStop()
        adapter.releasePlayer()
    }

}