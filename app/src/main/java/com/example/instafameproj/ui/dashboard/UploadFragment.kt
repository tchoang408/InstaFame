package com.example.instafameproj.ui.dashboard

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
import com.example.instafameproj.MainActivity
import com.example.instafameproj.databinding.FragmentUploadBinding
import com.example.instafameproj.ui.MediaUpload
import com.example.instafameproj.ui.userprofile.UserProfileViewModel

class UploadFragment : Fragment() {

    private var _binding: FragmentUploadBinding? = null
    private val viewModel: UserProfileViewModel by activityViewModels()
    private var selectedVideoUri : Uri? =null

    lateinit var mediaUpload: MediaUpload
    lateinit var videoLauncher: ActivityResultLauncher<Intent>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mainActivity: FragmentActivity
    private lateinit var context: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        mainActivity = (requireActivity() as MainActivity)
        context = requireContext()

        binding.uploadBt.setOnClickListener {
            checkPermissionAndOpenVideoPicker()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        mediaUpload = MediaUpload(requireActivity().activityResultRegistry, ::uploadMediaListener)
        lifecycle.addObserver(mediaUpload)
        videoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                selectedVideoUri = result.data?.data
                viewModel.uploadVideos(selectedVideoUri!!, viewModel.getUserMeta().uuid)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun uploadMediaListener(uri: Uri){
        //val resUri = Uri.parse(("android.resource://" +  mainActivity.packageName.toString() + "/" + R.raw.test1))
        viewModel.uploadVideos(uri,viewModel.getUserMeta().uuid)
    }


    private fun checkPermissionAndOpenVideoPicker(){
        var readExternalVideo : String = ""
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            readExternalVideo = android.Manifest.permission.READ_MEDIA_VIDEO
        }else{
            readExternalVideo = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if(ContextCompat.checkSelfPermission(this.context,readExternalVideo)== PackageManager.PERMISSION_GRANTED){
            //we have permission
            openVideoPicker()
        }else{
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(readExternalVideo),
                100
            )
        }
    }

    private fun openVideoPicker(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        videoLauncher.launch(intent)
    }



}