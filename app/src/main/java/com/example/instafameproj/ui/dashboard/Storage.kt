package com.example.instafameproj.ui.dashboard

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference



// Store files in firebase storage
class Storage {
    // Create a storage reference from our app
    private val photoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("videos")

    // https://firebase.google.com/docs/storage/android/upload-files#upload_from_a_local_file
    fun uploadImage(uri: Uri, uuid: String, timeStamp: String, uploadSuccess:(Long)->Unit) {

        //val file = Uri.fromFile(localFile)
        val uuidRef = photoStorage.child(uuid).child("test.mp4")
        val metadata = StorageMetadata.Builder()
            .setContentType("video/mp4")
            .build()
        val uploadTask = uuidRef.putFile(uri, metadata)
            .addOnSuccessListener {
                uuidRef.downloadUrl.addOnSuccessListener {downloadUrl->
                    //video model store in firebase firestore
                    Log.d("post_video" ,downloadUrl.toString())
                }
            }

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
                val sizeBytes = it.metadata?.sizeBytes ?: -1
                uploadSuccess(sizeBytes)
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
        photoStorage.child(pictureUUID).delete()
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
        return photoStorage.child(uuid)
    }


    fun getVideos(uuid:String, resultListener: (List<StorageReference>)->Unit){
        val uidRef = photoStorage.child(uuid)
        Log.d("download_url",uuid)
        uidRef.downloadUrl
        uidRef.listAll()
            .addOnSuccessListener {
                var a = listOf(it.items)
                for(video in it.items){
                    video.downloadUrl.addOnSuccessListener {
                        Log.d("download_url_", it.toString())
                    }

                }
                resultListener(a[0])
            }
            .addOnFailureListener {
                Log.d("download_url",it.message.toString())
            }
    }

}