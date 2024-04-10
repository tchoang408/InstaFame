package com.example.instafameproj.ui

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MediaUpload (private val registry : ActivityResultRegistry,
                   private val resultListener: (uri: Uri)->Unit)
    : DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<String>
    override fun onCreate(owner: LifecycleOwner) {

        getContent = registry.register("key", owner, ActivityResultContracts.GetContent())
        { uri ->

                // Photo selected, handle the Uri accordingly
            if (uri != null) {
                resultListener(uri)
            }
        }
    }
    fun checkPermissionAndPickPhoto(context: Context, fragment: Fragment,){
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
                fragment.requireActivity(),
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

}

