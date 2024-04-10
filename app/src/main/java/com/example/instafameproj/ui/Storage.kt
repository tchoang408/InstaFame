package com.example.instafameproj.ui

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.example.instafameproj.ui.Model.VideoModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference


// Store files in firebase storage
class Storage {
    // Create a storage reference from our app
    private val photoStorageVideo: StorageReference =
        FirebaseStorage.getInstance().reference.child("videos")
    private val photoStoragePic: StorageReference =
        FirebaseStorage.getInstance().reference.child("picture")
    private val dbhelpter = ViewModelDBHelper()
    companion object{
        var URL = ""
    }
    // https://firebase.google.com/docs/storage/android/upload-files#upload_from_a_local_file
    fun uploadVideoStorage(uri:Uri, videoModel: VideoModel,  uploadSuccess:(Long)->Unit) {
        val uuidRef = photoStorageVideo.child(videoModel.uuid).child("${videoModel.createdTime.seconds}.mp4")
        val metadata = StorageMetadata.Builder()
            .setContentType("video/mp4")
            .build()

        val uploadTask = uuidRef.putFile(uri, metadata)

        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnFailureListener {
                // Handle unsuccessful uploads
                /*
                if(localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file delete FAILED")
                }
                 */
            }
            .addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.

                uuidRef.downloadUrl.addOnSuccessListener {downloadUrl->
                    Log.d("post_video" ,downloadUrl.toString())
                    URL = downloadUrl.toString()

                    val sizeBytes = it.metadata?.sizeBytes ?: -1
                    dbhelpter.updateUserVideoUrl(URL.toString(),videoModel.uuid){
                        uploadSuccess(sizeBytes)
                    }

                    videoModel.url = URL.toString()
                    dbhelpter.createVideoMeta(videoModel){
                        Log.d("create_video" , URL.toString())
                        uploadSuccess(sizeBytes)
                    }

                }





                /*
                if(localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file delete FAILED")
                }
                */
            }
    }
    // https://firebase.google.com/docs/storage/android/delete-files#delete_a_file
    fun deleteImage(pictureUUID: String) {
        // Delete the file
        // XXX Write me
        photoStorageVideo.child(pictureUUID).delete()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Deleted $pictureUUID")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Delete FAILED of $pictureUUID")
            }
    }
    fun fetch(storageReference: StorageReference, imageView: ImageView) {
        // Layout engine does not know size of imageView
        // Hardcoding this here is a bad idea.  What would be better?
        val width = 400
        val height = 400

    }


    fun uuid2StorageReference(uuid: String): StorageReference {
        return photoStorageVideo.child(uuid)
    }


    fun getVideos(uuid:String, resultListener: (List<StorageReference>)->Unit){
        val uidRef = photoStorageVideo.child(uuid)
        Log.d("download_url",uuid)
        uidRef.downloadUrl
        uidRef.listAll()
            .addOnSuccessListener {
                for(video in it.items){
                    video.downloadUrl.addOnSuccessListener {
                        Log.d("download_url_", it.toString())
                    }

                }
                resultListener(it.items)
            }
            .addOnFailureListener {
                Log.d("download_url",it.message.toString())
            }
    }

    fun uploadProfilePicsStorage(uri:Uri, uuid:String,  uploadSuccess:(Long)->Unit) {
        val uuidRef = photoStoragePic.child(uuid)
        val metadata = StorageMetadata.Builder()
            .setContentType("images/jpg")
            .build()

        val uploadTask = uuidRef.putFile(uri, metadata)

        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnFailureListener {
                // Handle unsuccessful uploads
                /*
                if(localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file delete FAILED")
                }
                 */
            }
            .addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.

                uuidRef.downloadUrl.addOnSuccessListener {downloadUrl->
                    Log.d("post_video" ,downloadUrl.toString())
                    URL = downloadUrl.toString()

                    val sizeBytes = it.metadata?.sizeBytes ?: -1
                    dbhelpter.updateUserPicsUrl(URL.toString(),uuid){
                        uploadSuccess(sizeBytes)
                    }
                }





                /*
                if(localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file delete FAILED")
                }
                */
            }
    }

}