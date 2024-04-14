package com.example.instafameproj.ui.userprofile

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.example.instafameproj.ui.UserProfileViewModel

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val viewModel: UserProfileViewModel by activityViewModels()
    private lateinit var mainActivity: FragmentActivity
    private lateinit var adapter: UserVideosListAdapter
    private val binding get() = _binding!!
    private lateinit var context:Context
    lateinit var photoLauncher: ActivityResultLauncher<Intent>

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

        viewModel.fetchUserMeta(){

        }
        binding.editProfileBT.setOnClickListener {
            findNavController().navigate(R.id.action_toEditProfile)
        }

        binding.userProfilePic.setOnClickListener {
           checkPermissionAndPickPhoto()
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
            binding.folowersCount.text = viewModel.getUserMeta().followerList.size.toString()
            binding.folowingCount.text = viewModel.getUserMeta().followingList.size.toString()
            binding.likesCounts.text = viewModel.getUserMeta().likesCount.toString()

            val list = mutableListOf<VideoModel>()
            for( i in it.videoUrl.indices){
                val videoData = VideoModel(
                    url = it.videoUrl[i],
                    videoId = it.videoId[i],
                    uuid = it.uuid
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
        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                uploadMediaListener(result.data?.data!!)
            }
        }
    }
    private fun initRecyclerViewGrid() {
        adapter = UserVideosListAdapter(viewModel){

        }
        binding.videosRV.adapter = adapter

    }

    private fun uploadMediaListener(uri: Uri){
        viewModel.uploadPics(uri,viewModel.getUserMeta().uuid)
    }

    fun checkPermissionAndPickPhoto(){
        var readExternalPhoto : String = ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readExternalPhoto = android.Manifest.permission.READ_MEDIA_IMAGES
        }else{
            readExternalPhoto = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if(ContextCompat.checkSelfPermission(this.context,readExternalPhoto)== PackageManager.PERMISSION_GRANTED){
            //we have permission
            openPhotoPicker()
        }else{
            ActivityCompat.requestPermissions(
                this.mainActivity,
                arrayOf(readExternalPhoto),
                100
            )
        }
    }

    private fun openPhotoPicker(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        photoLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}